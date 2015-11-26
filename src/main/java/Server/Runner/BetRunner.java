package Server.Runner;

import Common.Models.Bet;
import Common.Models.GameInfo;
import Server.Server;

/**
 * Created by Michaël on 11/25/2015.
 */
public class BetRunner implements Runnable {

    private final Server server;
    private final GameInfo info;
    private final Bet bet;
    private final int gameId;

    public BetRunner(Server server, int gameId, GameInfo info, Bet bet) {
        this.server = server;
        this.gameId = gameId;
        this.info = info;
        this.bet = bet;
    }

    @Override
    public void run() {

        while (info.getPeriod() != 3 && info.getPeriodChronometer() != 0) {
            server.LockForUpdate();
            try {
                server.holdBet(gameId);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } finally {
                server.UnlockUpdates();
            }
        }

        bet.setAmountGained(server.ComputeAmountGained(bet, gameId));

//        //TODO Send notification to client
//        ServerMessage serverMessage = new ServerMessage(ServerMessageType.BetResult,
//                message.GetIPAddress(),
//                message.GetPort(),
//                message.getReceiverIp(),
//                message.getReceiverPort(),
//                message.getID(),
//                bet);
//
//        while (!(acks.containsKey(game.getGameID())
//                && acks.get(game.getGameID()).containsKey(message.GetIPAddress())
//                && acks.get(game.getGameID()).get(message.GetIPAddress()).containsKey(message.GetPort()))) {
//
//            try {
//                socket.Send(serverMessage);
//            } catch (IOException e) {
//                System.out.println("PlaceBet: Error sending message requiring ack.");
//                e.printStackTrace();
//                return;
//            }
//
//            try {
//                acksCondition.await(5, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
