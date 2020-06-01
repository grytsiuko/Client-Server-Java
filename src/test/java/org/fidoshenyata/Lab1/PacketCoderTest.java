package org.fidoshenyata.Lab1;

import com.github.snksoft.crc.CRC;
import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.packet.Message;
import org.fidoshenyata.packet.Packet;
import org.fidoshenyata.exceptions.cryption.TooLongMessageException;
import org.fidoshenyata.exceptions.packet.HalfPacketException;
import org.fidoshenyata.exceptions.packet.InvalidCRC16_1_Exception;
import org.fidoshenyata.exceptions.packet.InvalidCRC16_2_Exception;
import org.fidoshenyata.exceptions.packet.InvalidMagicByteException;
import org.fidoshenyata.packet.PacketCoder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import java.nio.ByteBuffer;
import java.security.Key;

public class PacketCoderTest {

    private static Key key;
    private static CRC crcInstance;
    private static Packet packet;
    private static PacketCoder packetCoder;
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
                .packetID(UnsignedLong.valueOf(2))
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(888)
                                .message("Hello World!")
                                .build()
                );
        packet = packetBuilder.build();

        packetCoder = new PacketCoder(key);
    }

    @Before
    public void setUPEncoder() throws Exception {
        byteArray = packetCoder.encode(packet);
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
        Assert.assertEquals("Length should be correct", buffer.capacity(), 26 + messageLength);
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
        byte[] message = new byte[messageLength];
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
        byte[] message = new byte[messageLength + 8];
        buffer.position(16);
        buffer.get(message);
        short calculatedCRC = (short) crcInstance.calculateCRC(message);

        short packetCRC = buffer.getShort();
        Assert.assertEquals("Message CRC should be equal", calculatedCRC, packetCRC);
    }

    @Test
    public void decodeWithKeySuccess() throws Exception {
        Assert.assertEquals(packetCoder.decode(byteArray), packet);
    }

    @Test(expected = InvalidMagicByteException.class)
    public void decodeCorruptPacketFailsMagicByte() throws Exception {
        byte[] corruptedByteArray = byteArray.clone();
        corruptedByteArray[0] = 0x14;
        packetCoder.decode(corruptedByteArray);
    }

    @Test(expected = InvalidCRC16_1_Exception.class)
    public void decodeCorruptPacketFailsCRC_1() throws Exception {
        byte[] corruptedByteArray = byteArray.clone();
        corruptedByteArray[7] = 12;
        packetCoder.decode(corruptedByteArray);
    }

    @Test(expected = InvalidCRC16_2_Exception.class)
    public void decodeCorruptPacketFailsCRC_2() throws Exception {
        byte[] corruptedByteArray = byteArray.clone();
        corruptedByteArray[20] = 12;
        packetCoder.decode(corruptedByteArray);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullPacketTest() throws Exception {
        packetCoder.encode(null);
    }

    @Test(expected = IllegalStateException.class)
    public void nullKeyTest() throws Exception {
        new PacketCoder(null);
    }

    @Test(expected = HalfPacketException.class)
    public void halfPacketDecodeTest() throws Exception {
        byte[] bytes = {0x13, 1, 2, 3, 4};
        packetCoder.decode(bytes);
    }

    @Test(expected = TooLongMessageException.class)
    public void longMessageTest() throws Exception {

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < Message.MAX_MESSAGE_LENGTH + 10; i++){
            stringBuilder.append("a");
        }

        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 5)
                .packetID(UnsignedLong.valueOf(2))
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(888)
                                .message(stringBuilder.toString())
                                .build()
                );
        packetCoder.encode(packetBuilder.build());
    }
}