package Server;

import Common.Communication.ClientMessage;
import Common.Communication.ServerMessageType;

/**
 * Micha�l Beaulieu         13048132
 * Benoit Jeunehomme        13055392
 * Bruno-Pier Touchette     13045732
 */
public class HandlerThread implements Runnable {

    private Server server;
    private ClientMessage clientMessage;

    public HandlerThread(Server server, ClientMessage clientMessage){
        this.server = server;
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        Object replyData;

        switch (clientMessage.getType()) {
            case GetMatches:
                replyData = server.GetGames();
                server.SendReply(ServerMessageType.ReturnGames, clientMessage, replyData);
                break;
            case GetMatchInfo:
                replyData = server.GetGameInfo(clientMessage.getData());
                server.SendReply(ServerMessageType.ReturnGameInfo, clientMessage, replyData);
                break;
            case PlaceBet:
                //server.PlaceBet(clientMessage.getData(), clientMessage);
                break;
            case AckNotification:
                server.AddAck(clientMessage);
                break;
            default:
                break;
        }

    }
}