package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

import java.net.Socket;
import java.security.Key;

public class Client {

    private Socket socket;

    public Client() {
    }

    public Packet send(int serverPort, Packet packet) throws Exception {
        try {

            socket = new Socket("localhost", serverPort);
            System.out.println("Client Connected");

            var in = socket.getInputStream();
            var out = socket.getOutputStream();

            Key key = new Keys().doHandShake(in, out);
            NetworkUtils networkUtils = new NetworkUtils(key);

//            networkUtils.sendMessage(packet, out);
//            System.out.println("Client sent");
//
//            Packet reply = networkUtils.receiveMessage(in);
//            System.out.println("Client received");

            in.close();
            out.close();
//            return reply;
            return null;
        } finally {
            System.out.println("Client closed");
            socket.close();
        }

    }

    public static void main(String[] args) throws Exception {
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

        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                try {
                    Client client = new Client();
                    Packet reply = client.send(Server.PORT, packet);
//                    System.out.println(reply.getUsefulMessage().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
