package org.fidoshenyata;

import com.github.snksoft.crc.CRC;
import org.fidoshenyata.model.Packet;
import org.fidoshenyata.model.PacketBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class PacketEncoderTest {

    private static Key key;
    private static PacketEncoder packetEncoder;
    private static Packet packet;
    private static CRC crcInstance;
    private static Cipher cipher;
    private ByteBuffer buffer;

    @BeforeClass
    public static void setUP() throws Exception {

        String algorithm = "AES";
        cipher = Cipher.getInstance(algorithm);

        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(128);
        key = keyGen.generateKey();

        crcInstance = new CRC(CRC.Parameters.CRC16);

        PacketBuilder packetBuilder = new PacketBuilder()
                .setSource((byte) 5)
                .setUserID(2048)
                .setCommandType(888)
                .setPacketID((long) 2)
                .setMessage("Hello World!");
        packet = packetBuilder.build();
    }

    @Before
    public void setUPEncoder() throws Exception {
        packetEncoder = new PacketEncoder()
                .setKey(key)
                .setAlgorithm("AES")
                .setPacket(packet);

        byte[] bytes = packetEncoder.encode();
        buffer = ByteBuffer.wrap(bytes);
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

    @Test(expected = NoSuchAlgorithmException.class)
    public void wrongCipherAlgorithmTest() throws Exception {
        packetEncoder.setAlgorithm("wrong algorithm");
        packetEncoder.encode();
    }

    @Test(expected = IllegalStateException.class)
    public void nullCipherAlgorithmTest() throws Exception {
        PacketEncoder packetEncoder = new PacketEncoder()
                .setPacket(packet)
                .setKey(key);
        packetEncoder.encode();
    }

    @Test(expected = IllegalStateException.class)
    public void nullPacketTest() throws Exception {
        packetEncoder.setPacket(null);
        packetEncoder.encode();
    }

    @Test(expected = IllegalStateException.class)
    public void nullKeyTest() throws Exception {
        packetEncoder.setKey(null);
        packetEncoder.encode();
    }
}
