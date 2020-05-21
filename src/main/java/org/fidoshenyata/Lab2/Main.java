package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

public class Main {

    public static void main(String[] args) throws Exception {

        new Thread(() -> {
            try {
                Server server = new Server(59898);
                server.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

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

        Client client = new Client();
        Packet reply = client.send(59898, packet);
//        System.out.println(reply.getUsefulMessage().toString());

    }
}
