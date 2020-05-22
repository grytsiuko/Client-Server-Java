package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

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

            Key key = doHandShake(in, out);
            NetworkUtils networkUtils = new NetworkUtils(key);

            networkUtils.sendMessage(packet, out);
            System.out.println("Client sent");

            Packet reply = networkUtils.receiveMessage(in);
            System.out.println("Client received");

            in.close();
            out.close();
            return reply;
        } finally {
            System.out.println("Client closed");
            socket.close();
        }

    }

    private Key doHandShake(InputStream inputStream, OutputStream outputStream) throws Exception {
        Keys keys = new Keys();

        PublicKey publicKey = keys.getPublicKey();
        byte[] publicKeyEncoded = publicKey.getEncoded();

        outputStream.write(publicKeyEncoded);
        outputStream.write(0x13);
        outputStream.flush();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int oneChar; (oneChar = inputStream.read()) != 0x13; )
            buffer.write(oneChar);

        PublicKey serverPublicKey =
                KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(buffer.toByteArray()));
        keys.setReceiverPublicKey(serverPublicKey);

        return keys.generateKey();
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
        for (int i = 0; i < 10; i++) {
            Packet reply = client.send(Server.PORT, packet);
            System.out.println(reply.getUsefulMessage().toString());
        }
    }

}
