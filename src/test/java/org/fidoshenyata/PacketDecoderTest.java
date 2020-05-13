package org.fidoshenyata;

import org.fidoshenyata.model.Packet;
import org.fidoshenyata.model.PacketBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.KeyGenerator;
import java.security.Key;

public class PacketDecoderTest {

    private static Key key;
    private static KeyGenerator keyGen;
    private static PacketDecoder packetDecoder;
    private static Packet packet;
    private static byte[] byteArray;

    @BeforeClass
    public static void setUP() throws Exception {

        String algorithm = "AES";
        keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(128);
        key = keyGen.generateKey();
        PacketBuilder packetBuilder = new PacketBuilder()
                .setSource((byte) 5)
                .setUserID(2048)
                .setCommandType(888)
                .setPacketID((long) 2)
                .setMessage("Hello World!");
        packet = packetBuilder.build();

        PacketEncoder packetEncoder = new PacketEncoder(algorithm)
                .setKey(key)
                .setPacket(packet);

        byteArray = packetEncoder.encode();

        packetDecoder = new PacketDecoder(algorithm);
    }

    @Test
    public void decodeWithKeySuccess() throws Exception {
        Assert.assertEquals(packetDecoder.setKey(key).decode(byteArray), packet);
    }

    @Test(expected = BadPaddingException.class)
    public void decodeWithWrongKeyFails() throws Exception {
        packetDecoder.setKey(keyGen.generateKey()).decode(byteArray);
    }

    @Test(expected = IllegalStateException.class)
    public void decodeWithOutKeyFails() throws Exception {
        packetDecoder.setKey(null).decode(byteArray);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeCorruptPacketFails() throws Exception {
        byte[] corruptedByteArray = byteArray.clone();
        corruptedByteArray[7] = 12;
        packetDecoder.setKey(key).decode(corruptedByteArray);
    }
}