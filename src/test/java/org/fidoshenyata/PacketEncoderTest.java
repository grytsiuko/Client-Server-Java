package org.fidoshenyata;

import com.github.snksoft.crc.CRC;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.nio.ByteBuffer;
import java.security.Key;

public class PacketEncoderTest {

    private static Key key;
    private static PacketEncoder packetEncoder;
    private static CRC crcInstance;
    private static Cipher cipher;

    @BeforeClass
    public static void setUP() throws Exception {

        String algorithm = "AES";
        cipher = Cipher.getInstance(algorithm);

        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(128);
        key = keyGen.generateKey();

        crcInstance = new CRC(CRC.Parameters.CRC16);

        packetEncoder = new PacketEncoder()
                .setKey(key)
                .setAlgorithm("AES")
                .setSource((byte) 5)
                .setUserID(2048)
                .setCommandType(888)
                .setPacketID(2)
                .setMessage("Hello World!");
    }

    @Test
    public void magicNumberTest() throws Exception {

        byte[] bytes = packetEncoder.encode();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        Assert.assertEquals("Magic number should be equal", buffer.get(0), 0x13);
    }

    @Test
    public void sourceTest() throws Exception {

        byte[] bytes = packetEncoder.encode();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        Assert.assertEquals("Source should be equal", buffer.get(1), 5);
    }

    @Test
    public void packetIDTest() throws Exception {

        byte[] bytes = packetEncoder.encode();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        Assert.assertEquals("Packet ID should be equal", buffer.getLong(2), 2);
    }

    @Test
    public void packetLengthTest() throws Exception {

        byte[] bytes = packetEncoder.encode();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        int messageLength = buffer.getInt(10);
        Assert.assertEquals("Length should be correct", buffer.capacity(), 18 + messageLength);
    }

    @Test
    public void firstCRCTest() throws Exception {

        byte[] bytes = packetEncoder.encode();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        byte[] metadata = new byte[14];
        buffer.get(metadata);
        short calculatedCRC = (short)crcInstance.calculateCRC(metadata);

        short packetCRC = buffer.getShort();
        Assert.assertEquals("Metadata CRC should be equal", calculatedCRC, packetCRC);
    }

    @Test
    public void commandTypeTest() throws Exception {

        byte[] bytes = packetEncoder.encode();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        Assert.assertEquals("Command Type should be equal", buffer.getInt(16), 888);
    }

    @Test
    public void userIDTest() throws Exception {

        byte[] bytes = packetEncoder.encode();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        Assert.assertEquals("User ID should be equal", buffer.getInt(20), 2048);
    }

    @Test
    public void messageTest() throws Exception {

        byte[] bytes = packetEncoder.encode();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

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
    public void secondCRCTest() throws Exception {

        byte[] bytes = packetEncoder.encode();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        int messageLength = buffer.getInt(10);
        byte[] message = new byte[messageLength - 8];
        buffer.position(24);
        buffer.get(message);
        short calculatedCRC = (short)crcInstance.calculateCRC(message);

        short packetCRC = buffer.getShort();
        Assert.assertEquals("Message CRC should be equal", calculatedCRC, packetCRC);
    }
}
