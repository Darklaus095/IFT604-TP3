package Common.Communication;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Micha�l Beaulieu         13048132
 * Benoit Jeunehomme        13055392
 * Bruno-Pier Touchette     13045732
 */
public class ServerMessage implements Serializable {
    private ServerMessageType type;

    private InetAddress senderIp;
    private int senderPort;
    private InetAddress receiverIp;
    private int receiverPort;
    private int requestID;
    private Object data;

    public ServerMessage(ServerMessageType type, InetAddress ip, int port, InetAddress receiverIp, int receiverPort, int requestID, Object data) {
        this.type = type;
        senderIp = ip;
        senderPort = port;
        this.receiverIp = receiverIp;
        this.receiverPort = receiverPort;
        this.requestID = requestID;
        this.data = data;
    }

    public InetAddress getSenderIp() {
        return senderIp;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public InetAddress getReceiverIp() {
        return receiverIp;
    }

    public int getReceiverPort() {
        return receiverPort;
    }

    public Object getData() {
        return data;
    }

    public int getRequestID() {
        return requestID;
    }


    public ServerMessageType getType() {
        return type;
    }
}
