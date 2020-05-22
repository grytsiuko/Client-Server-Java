package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

import java.net.Socket;

public class Client {

    private NetworkProtocol networkProtocol;
    private Socket socket;

    public Packet send(int serverPort, Packet packet) throws Exception{
        try {

            socket = new Socket("localhost", serverPort);
            System.out.println("Client Connected");

            var in = socket.getInputStream();
            var out = socket.getOutputStream();

            networkProtocol.sendMessage(packet, out);
            System.out.println("Client sent");
            Packet reply = networkProtocol.receiveMessage(in);
            System.out.println("Client received");
            in.close();
            out.close();
            return reply;
        } finally {
            System.out.println("Client closed");
            socket.close();
        }

    }

    public Client() throws Exception {
        this.networkProtocol = new NetworkProtocolTCP();
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

        Client client = new Client();
        Packet reply = client.send(Server.PORT, packet);
        System.out.println(reply.getUsefulMessage().toString());
    }

}
