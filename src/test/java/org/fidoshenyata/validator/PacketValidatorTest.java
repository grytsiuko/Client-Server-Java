package org.fidoshenyata.validator;

import org.fidoshenyata.PacketEncoder;
import org.fidoshenyata.model.Packet;
import org.fidoshenyata.model.PacketBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.KeyGenerator;
import java.security.Key;

public class PacketValidatorTest {
    private static PacketEncoder packetEncoder;
    private static byte[] bytesArray;
    private static Validator<byte[]> validator;

    @BeforeClass
    public static void setUP() throws Exception {

        String algorithm = "AES";
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(128);
        Key key = keyGen.generateKey();

        PacketBuilder packetBuilder = new PacketBuilder()
                .setSource((byte) 5)
                .setUserID(2048)
                .setCommandType(888)
                .setPacketID((long) 2)
                .setMessage("Hello World!");
        Packet packet = packetBuilder.build();
        packetEncoder = new PacketEncoder()
                .setKey(key)
                .setAlgorithm("AES")
                .setPacket(packet);

        validator = new PacketValidator();
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
    public void packetIsInvalid() {
        bytesArray[1] = 67;
        Assert.assertFalse(validator.isValid(bytesArray));
    }

}