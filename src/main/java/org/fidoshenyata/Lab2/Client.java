package org.fidoshenyata.Lab2;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

import java.io.IOException;
import java.net.Socket;

public class Client {

    private Socket socket;
    private NetworkUtils networkUtils;

    public Client() {
    }

    public void connect(int serverPort) throws IOException {
        socket = new Socket("localhost", serverPort);
        networkUtils = new NetworkUtils(socket);
    }

    public Packet request(Packet packet) throws IOException {
        if (networkUtils == null) {
            throw new IllegalStateException("Not connected yet");
        }

        networkUtils.sendMessage(packet);
        return networkUtils.receiveMessage();
    }

    public void disconnect() throws IOException {
        networkUtils.closeStreams();
        socket.close();
    }

    public static void main(String[] args) {
        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 5)
                .packetID(UnsignedLong.valueOf(2))
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(Message.CommandTypes.ADD_PRODUCT.ordinal())
                                .message("Hello From Client!")
                                .build()
                );
        Packet packet = packetBuilder.build();

        final int threads = 2;
        final int packetsInThread = 50;

        for (int k = 0; k < threads; k++) {
            new Thread(() -> {
                try {
                    Client client = new Client();
                    client.connect(Server.PORT);

                    int succeed = 0;
                    for (int i = 0; i < packetsInThread; i++) {
                        Packet response = client.request(packet);
                        String message = response.getUsefulMessage().getMessage();
                        if (!message.equals("Ok"))
                            System.out.println("Wrong response: " + message);
                        else
                            succeed++;
                    }

                    System.out.println(succeed + " of " + packetsInThread + " are succeed");
                    client.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
