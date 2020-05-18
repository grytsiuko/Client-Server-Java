package org.fidoshenyata.Lab1;

import com.github.snksoft.crc.CRC;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import java.nio.ByteBuffer;
import java.security.Key;

import static org.junit.Assert.*;

public class PacketCoderTest {

    private static Key key;
    private static CRC crcInstance;
    private static Packet packet;
    private static PacketCoder packetEncoder;
    private static PacketCoder packetDecoder;
    private ByteBuffer buffer;
    private static Cipher cipher;
    private static byte[] byteArray;

    @BeforeClass
    public static void setUP() throws Exception {
        cipher = Cipher.getInstance("AES");
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        key = keyGen.generateKey();

        crcInstance = new CRC(CRC.Parameters.CRC16);

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
        packet = packetBuilder.build();

        packetEncoder =  new PacketCoder(key, Cipher.ENCRYPT_MODE);
        packetDecoder =  new PacketCoder(key, Cipher.DECRYPT_MODE);
    }

    @Before
    public void setUPEncoder() throws Exception {
        byteArray = packetEncoder.encode(packet);
        buffer = ByteBuffer.wrap(byteArray);
    }

    @Test
    public void magicNumberTest() {
        Assert.assertEquals("Magic number should be equal", buffer.get(0), 0x13);
    }

    @Test
    public void sourceTest() {
        Assert.assertEquals("Source should be equal", buffer.get(1), 5);
    }

    @Test
    public void packetIDTest() {
        Assert.assertEquals("Packet ID should be equal", buffer.getLong(2), 2);
    }

    @Test
    public void packetLengthTest() {
        int messageLength = buffer.getInt(10);
        Assert.assertEquals("Length should be correct", buffer.capacity(), 18 + messageLength);
    }

    @Test
    public void firstCRCTest() {
        byte[] metadata = new byte[14];
        buffer.get(metadata);
        short calculatedCRC = (short) crcInstance.calculateCRC(metadata);

        short packetCRC = buffer.getShort();
        Assert.assertEquals("Metadata CRC should be equal", calculatedCRC, packetCRC);
    }

    @Test
    public void commandTypeTest() {
        Assert.assertEquals("Command Type should be equal", buffer.getInt(16), 888);
    }

    @Test
    public void userIDTest() {
        Assert.assertEquals("User ID should be equal", buffer.getInt(20), 2048);
    }

    @Test
    public void messageTest() throws Exception {
        int messageLength = buffer.getInt(10);
        byte[] message = new byte[messageLength - 8];
        buffer.position(24);
        buffer.get(message);

        cipher.init(Cipher.DECRYPT_MODE, key);
        String decryptedMessage = new String(cipher.doFinal(message));
        Assert.assertEquals("Message should not be corrupted",
                "Hello World!", decryptedMessage);
    }

    @Test
    public void secondCRCTest() {
        int messageLength = buffer.getInt(10);
        byte[] message = new byte[messageLength];
        buffer.position(16);
        buffer.get(message);
        short calculatedCRC = (short) crcInstance.calculateCRC(message);

        short packetCRC = buffer.getShort();
        Assert.assertEquals("Message CRC should be equal", calculatedCRC, packetCRC);
    }

    @Test
    public void decodeWithKeySuccess() throws Exception {
        Assert.assertEquals(packetDecoder.decode(byteArray), packet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeCorruptPacketFails() throws Exception {
        byte[] corruptedByteArray = byteArray.clone();
        corruptedByteArray[7] = 12;
        packetDecoder.decode(corruptedByteArray);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullPacketTest() throws Exception {
        packetEncoder.encode(null);
    }

    @Test(expected = IllegalStateException.class)
    public void nullKeyTest() throws Exception {
        new PacketCoder(null,1);
    }
}