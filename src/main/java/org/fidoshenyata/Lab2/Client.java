package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

import java.net.Socket;

public class Client {

    private Socket socket;

    public Client() {
    }

    public Packet send(int serverPort, Packet packet) throws Exception {
        try {

            socket = new Socket("localhost", serverPort);
//            System.out.println("Client Connected");

            NetworkUtils networkUtils = new NetworkUtils(socket);

            networkUtils.sendMessage(packet);
//            System.out.println("Client sent");

            Packet reply = networkUtils.receiveMessage();
//            System.out.println("Client received");

            networkUtils.closeStreams();
            return reply;
        } finally {
//            System.out.println("Client closed");
            socket.close();
        }

    }

    public static void main(String[] args) {
        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 5)
                .packetID((long) 2)
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(888)
                                .message("Hello From Client!")
                                .build()
                );
        Packet packet = packetBuilder.build();

        final int threads = 5;
        final int packetsInThread = 1000;

        for (int k = 0; k < threads; k++) {
            new Thread(() -> {
                Client client = new Client();
                int broken = 0;
                for (int i = 0; i < packetsInThread; i++) {
                    try {
                        Packet reply = client.send(Server.PORT, packet);
//                        System.out.println(i + " " + reply.getUsefulMessage().getMessage());
                    } catch (Exception e) {
//                        e.printStackTrace();
                        broken++;
                    }
                }
                System.out.println(broken + " of " + packetsInThread + " are broken");
            }).start();
        }
    }

}
