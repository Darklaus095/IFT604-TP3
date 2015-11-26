package Services;

import Common.Models.Game;
import Server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Michaël on 11/25/2015.
 */
public class ServerService {
    private static final Logger logger = LoggerFactory.getLogger(ServerService.class);

    private static ServerService instance;

    private Server server;

    private ServerService(Server server){
        this.server = server;
    }

    public static ServerService getInstance(){
        return instance;
    }

    public static void initialize(Server server){
        instance = new ServerService(server);
    }

    public void start(){
        logger.info("Starting server");
        server.start();
    }

    public void stop(){
        logger.info("Stopping server");
        server.stop();
    }

    public List<Game> getGames(){
        return server.GetGames();
    }

//     TODO:
//     case GetMatches:
//     replyData = server.GetGames();
//     server.SendReply(ServerMessageType.ReturnGames, clientMessage, replyData);
//     break;
//     case GetMatchInfo:
//     replyData = server.GetGameInfo(clientMessage.getData());
//     server.SendReply(ServerMessageType.ReturnGameInfo, clientMessage, replyData);
//     break;
//     case PlaceBet:
//     server.PlaceBet(clientMessage.getData(), clientMessage);
//     break;
//     case AckNotification:
//     server.AddAck(clientMessage);



}
