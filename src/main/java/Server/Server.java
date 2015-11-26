package Server;

import Common.Communication.ClientMessage;
import Common.Communication.ServerMessage;
import Common.Communication.ServerMessageType;
import Common.Constants;
import Common.Models.Bet;
import Common.Models.Game;
import Common.Models.GameInfo;
import Server.Communication.ServerSocket;
import Server.Factory.GameFactory;
import Server.Runner.BetRunner;
import Server.Runner.Chronometer;
import Server.Runner.GameEventUpdater;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Micha�l Beaulieu         13048132
 * Benoit Jeunehomme        13055392
 * Bruno-Pier Touchette     13045732
 */
public class Server implements Runnable {
    private static int UPDATE_INTERVAL = 30;    //in seconds

    private List<Game> runningGames;
    private ConcurrentMap<Integer, Condition> gamesCompleted;
    private ConcurrentMap<Integer, GameInfo> runningGameInfos;
    private ConcurrentMap<Integer, List<Bet>> placedBets;

    private ConcurrentMap<Integer, ConcurrentMap<InetAddress, ConcurrentMap<Integer, ClientMessage>>> acks;
    private Condition acksCondition;
    private ServerSocket socket;

    private Thread serverThread;
    private Chronometer chronometer;
    private GameEventUpdater eventUpdater;
    private List<Thread> betWatchers;

    private Lock gameUpdateLock;

    public Server() {
        runningGames = new ArrayList<>();
        runningGameInfos = new ConcurrentHashMap<>();
        placedBets = new ConcurrentHashMap<>();
        gameUpdateLock = new ReentrantLock();
        acks = new ConcurrentHashMap<>();
        gamesCompleted = new ConcurrentHashMap<>();
        betWatchers = new ArrayList<>();
        Initialize();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void execute() {
        try {
            socket = new ServerSocket(Constants.SERVER_COMM_PORT);
            ExecutorService threadPool = Executors.newCachedThreadPool();

            while (true) {

                try {
                    ClientMessage clientMessage = socket.GetMessage();

                    Runnable handler = new HandlerThread(this, clientMessage);
                    threadPool.submit(handler);
                } catch (InterruptedException e) {
                    System.out.println("Thread receiving message interrupted");
                    socket.CloseSocket();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SendReply(ServerMessageType type, ClientMessage clientMessage, Object data) {
        ServerMessage serverMessage = new ServerMessage(type, clientMessage.GetIPAddress(),
                clientMessage.GetPort(),
                clientMessage.getReceiverIp(),
                clientMessage.getReceiverPort(),
                clientMessage.getID(),
                data);

        try {
            socket.Send(serverMessage);
        } catch (IOException e) {
            System.out.println("SendReply : Socket error occured - Not sending message");
            e.printStackTrace();
            return;
        }
    }

    public synchronized void AddGame(Game game, GameInfo info) {
        if (runningGames.size() < 10) {
            runningGames.add(game);
            runningGameInfos.put(game.getGameID(), info);
            placedBets.put(game.getGameID(), new ArrayList<>());
            acks.put(game.getGameID(), new ConcurrentHashMap<>());
            gamesCompleted.put(game.getGameID(), gameUpdateLock.newCondition());
        }
    }

    public synchronized List<Game> GetGames() {
        return runningGames;
    }

    public synchronized List<GameInfo> GetGameInfos() {
        return runningGameInfos.values().stream().collect(Collectors.toList());
    }

    public synchronized List<Game> GetNonCompletedGames() {
        return runningGames.stream().filter(g -> !g.isCompleted()).collect(Collectors.toList());
    }

    public synchronized Game GetGameByID(Integer gameID) {
        for (int i = 0; i < runningGames.size(); i++) {
            Game g = runningGames.get(i);

            if (g.getGameID() == gameID)
                return g;
        }

        return null;
    }

    public synchronized List<Bet> GetGameBets(Game game) {
        return placedBets.get(game.getGameID());
    }

    public synchronized GameInfo GetGameInfo(Integer gameID) {
        return runningGameInfos.get(gameID);
    }

    public synchronized GameInfo GetGameInfo(Object gameID) {
        try {
            Integer g = (Integer) gameID;
            return GetGameInfo(g);
        } catch (Exception e) {
            return null;
        }
    }

    public void AddAck(ClientMessage message) {
        Bet bet = (Bet) message.getData();

        ConcurrentMap<InetAddress, ConcurrentMap<Integer, ClientMessage>> gameAcks = acks.get(message.getID());
        gameAcks.putIfAbsent(message.GetIPAddress(), new ConcurrentHashMap<>());
        ConcurrentMap<Integer, ClientMessage> addressAcks = gameAcks.get(message.GetIPAddress());
        addressAcks.put(message.GetPort(), message);

        acksCondition.signalAll();
    }

    public boolean PlaceBet(Bet bet) {
        Game game = GetGameByID(bet.getGameID());
        GameInfo info = runningGameInfos.get(game.getGameID());

        boolean added = false;

        if (info.getPeriod() <= 2) {
            added = true;
            (placedBets.get(bet.getGameID())).add(bet);
        }

        Thread t = new Thread(new BetRunner(this, game.getGameID(), info, bet));
        betWatchers.add(t);
        t.start();

        return added;
    }

    public void PlaceBet(Object bet, ClientMessage message) {
        try {
            Bet b = (Bet) bet;
            //PlaceBet(b, message);
        } catch (Exception e) {
            SendReply(ServerMessageType.BetConfirmation, message, false);
        }
    }

    public void holdBet(int gameId) throws InterruptedException {
        gamesCompleted.get(gameId).await();
    }

    public void notifyBets(Game game) {
        gamesCompleted.get(game.getGameID()).signalAll();
    }

    public double ComputeAmountGained(Bet bet, int gameId) {
        Game game = GetGameByID(gameId);
        List<Bet> bets = placedBets.get(game.getGameID());
        GameInfo info = GetGameInfo(game.getGameID());

        double totalAmountHost = 0;
        double totalAmountVisitor = 0;

        for (int i = 0; i < bets.size(); i++) {
            Bet b = bets.get(i);

            if (b.getBetOn().equals(game.getHost())) {
                totalAmountHost += b.getAmount();
            } else {
                totalAmountVisitor += b.getAmount();
            }
        }

        double amountGained = 0;

        if (info.getHostGoalsTotal() > info.getVisitorGoalsTotal()) {
            if (bet.getBetOn().equals(game.getHost())) {
                amountGained = 0.75 * (totalAmountHost + totalAmountVisitor)
                        * (bet.getAmount() / totalAmountHost);
            } else {
                amountGained = 0 - bet.getAmount();
            }
        } else if (info.getHostGoalsTotal() < info.getVisitorGoalsTotal()) {
            if (bet.getBetOn().equals(game.getVisitor())) {
                amountGained = 0.75 * (totalAmountHost + totalAmountVisitor)
                        * (bet.getAmount() / totalAmountVisitor);
            } else {
                amountGained = 0 - bet.getAmount();
            }
        }

        return amountGained;
    }

    @Override
    public void run() {
        execute();
    }

    public void start() {
        serverThread = new Thread(this);
        serverThread.start();
        chronometer = new Chronometer(UPDATE_INTERVAL, this);
        eventUpdater = new GameEventUpdater(UPDATE_INTERVAL, this);
    }

    public void Initialize() {
        runningGames.clear();
        runningGameInfos.clear();
        //acks.clear();
        placedBets.clear();
        GameFactory.Initialize();
        InitializeGames();
    }

    private void InitializeGames() {
        for (int i = 0; i < 10; ++i) {
            Game g = GameFactory.GenerateGame();
            GameInfo info = GameFactory.GenerateGameInfo(g);
            AddGame(g, info);
        }
    }

    public void stop() {
        for (Thread t : betWatchers) {
            if (t.isAlive())
                t.interrupt();
        }

        betWatchers.clear();
        eventUpdater.Stop();
        chronometer.Stop();
        if (socket != null) socket.CloseSocket();
        serverThread.interrupt();
    }

    public void LockForUpdate() {
        gameUpdateLock.lock();
    }

    public void UnlockUpdates() {
        gameUpdateLock.unlock();
    }

    public void SetPeriodLength(int minutes) {
        GameFactory.UpdatePeriodLength(minutes);
    }
}
