package org.fidoshenyata.Lab1;

import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;

public class Main {

    public static void main(String[] args) throws Exception {

        String algorithm = "AES";
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(128);
        Key key = keyGen.generateKey();

        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 5)
                .packetID((long) 2)
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(888)
                                .message("Hello World!")
                                .build()
                );
        Packet packet = packetBuilder.build();

        PacketCoder packetCoder = new PacketCoder(key);
        byte[] packetBytes = packetCoder.encode(packet);

        System.out.println(new String(packetBytes));
        System.out.println(packetCoder.decode(packetBytes).getUsefulMessage().getMessage());
    }
}
