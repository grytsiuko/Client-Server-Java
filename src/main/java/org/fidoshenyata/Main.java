package org.fidoshenyata;

import org.fidoshenyata.model.Packet;

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
                .userID(2048)
                .commandType(888)
                .packetID((long) 2)
                .message("Hello World!");
        Packet packet = packetBuilder.build();

        PacketEncoder packetEncoder = new PacketEncoder(algorithm)
                .setKey(key);

        PacketDecoder packetdecoder = new PacketDecoder(algorithm)
                .setKey(key);

        byte[] packetBytes = packetEncoder
                .setPacket(packet)
                .encode();

        System.out.println(new String(packetBytes));
        System.out.println(packetdecoder.decode(packetBytes).getMessage());
    }
}
