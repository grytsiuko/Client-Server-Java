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
                .setAlgorithm("AES");
    }

    @Test
    public void basicTest() throws Exception {

        byte[] bytes = packetEncoder
                .setSource((byte) 5)
                .setUserID(2048)
                .setCommandType(888)
                .setPacketID(2)
                .setMessage("Hello World!").encode();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        Assert.assertEquals("Magic number should be equal", buffer.get(), 0x13);
        Assert.assertEquals("Source should be equal", buffer.get(), 5);
        Assert.assertEquals("Packet ID should be equal", buffer.getLong(), 2);

        int messageLength = buffer.getInt();
        Assert.assertEquals("Length should be correct", buffer.capacity(), 18 + messageLength);

        byte[] crc1 = new byte[2];
        buffer.get(crc1);

        Assert.assertEquals("Command Type should be equal", buffer.getInt(), 888);
        Assert.assertEquals("User ID should be equal", buffer.getInt(), 2048);

        byte[] message = new byte[messageLength - 8];
        buffer.get(message);

        byte[] crc2 = new byte[2];
        buffer.get(crc2);

        Assert.assertArrayEquals("Both CRC should be equal", crc1, crc2);

        cipher.init(Cipher.DECRYPT_MODE, key);
        String decryptedMessage = new String(cipher.doFinal(message));
        Assert.assertEquals("Message should not be corrupted",
                "Hello World!", decryptedMessage);

        long messageCRC = crcInstance.calculateCRC(message);
        Assert.assertEquals("Less significant CRC byte should be correct",
                (byte) (messageCRC & 0xff), crc1[1]);
        Assert.assertEquals("More significant CRC byte should be correct",
                (byte) ((messageCRC >> 8) & 0xff), crc1[0]);
    }
}
