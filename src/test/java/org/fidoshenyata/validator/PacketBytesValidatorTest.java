package org.fidoshenyata.validator;

import org.fidoshenyata.PacketEncoder;
import org.fidoshenyata.model.Packet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.KeyGenerator;
import java.security.Key;

public class PacketBytesValidatorTest {

    private static PacketEncoder packetEncoder;
    private static byte[] bytesArray;
    private static Validator<byte[]> validator;

    @BeforeClass
    public static void setUP() throws Exception {

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
        packetEncoder = new PacketEncoder(algorithm)
                .setKey(key)
                .setPacket(packet);

        validator = new PacketBytesValidator();
    }

    @Before
    public void before() throws Exception {
        bytesArray = packetEncoder.encode();
    }

    @Test
    public void packetIsValid() {
        Assert.assertTrue(validator.isValid(bytesArray));
    }

    @Test
    public void packetIsInvalidFirstCRC() {
        bytesArray[1] = 67;
        Assert.assertFalse(validator.isValid(bytesArray));
    }

    @Test
    public void packetIsInvalidSecondCRC() {
        bytesArray[20] = 67;
        Assert.assertFalse(validator.isValid(bytesArray));
    }

}