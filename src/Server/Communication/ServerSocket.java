package Server.Communication;

import Common.Communication.ClientMessage;
import Common.Communication.ServerMessage;
import Common.helpers.SerializationHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Micha�l Beaulieu         13048132
 * Benoit Jeunehomme        13055392
 * Bruno-Pier Touchette     13045732
 */
public class ServerSocket {
    private DatagramSocket epSocket;
    private Thread tReceive;
    private BlockingQueue<ClientMessage> clientMessageBuffer = new ArrayBlockingQueue<>(50);

    public ServerSocket(int port) throws IOException {
        epSocket = new DatagramSocket(port);
        tReceive = new Thread(() -> {
            Receive();
        });
        tReceive.start();
    }

    public void Receive() {
        byte[] receiveData = new byte[4096];
        while (true) {
            DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
            try {
                if (tReceive.isInterrupted()) break;
                epSocket.receive(packet);
                ClientMessage req = new ClientMessage(packet);
                clientMessageBuffer.put(req);
            } catch (Exception e) {
                CloseSocket();
                break;
            }
        }
    }

    public void Send(ServerMessage serverMessage) throws IOException {
        byte[] data = SerializationHelper.serialize(serverMessage);
        DatagramPacket packet = new DatagramPacket(data, data.length, serverMessage.getReceiverIp(), serverMessage.getReceiverPort());
        epSocket.send(packet);
    }

    public ClientMessage GetMessage() throws InterruptedException {
        return clientMessageBuffer.take();
    }

    public void CloseSocket() {
        tReceive.interrupt();
        epSocket.close();
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Finalizing Server Socket");
        tReceive.interrupt();
        super.finalize();
    }
}
