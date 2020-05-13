package org.fidoshenyata;

import org.fidoshenyata.model.Packet;
import org.fidoshenyata.model.PacketBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PacketBuilderTest {

    private PacketBuilder packetBuilder;
    private Packet packet;

    @Before
    public void setUp() {
        packetBuilder = new PacketBuilder()
                .setPacketID((long)15)
                .setCommandType(13)
                .setMessage("Hello World!")
                .setSource((byte)4)
                .setUserID(999);
        packet = packetBuilder.build();
    }

    @Test
    public void packetIDTest() {
        Assert.assertEquals("Packet ID should be equal", packet.getPacketID(), 15);
    }

    @Test
    public void CommandTypeTest() {
        Assert.assertEquals("Command Type should be equal", packet.getCommandType(), 13);
    }

    @Test
    public void messageTest() {
        Assert.assertEquals("Message should be equal", packet.getMessage(), "Hello World!");
    }

    @Test
    public void sourceTest() {
        Assert.assertEquals("Source should be equal", packet.getSource(), 4);
    }

    @Test
    public void userIDTest() {
        Assert.assertEquals("User ID should be equal", packet.getUserID(), 999);
    }

    @Test(expected = IllegalStateException.class)
    public void nullPacketIDTest() {
        packetBuilder.setPacketID(null);
        packetBuilder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void nullCommandTypeTest() {
        packetBuilder.setCommandType(null);
        packetBuilder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void nullMessageTest() {
        packetBuilder.setMessage(null);
        packetBuilder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void nullSourceTest() {
        packetBuilder.setSource(null);
        packetBuilder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void nullUserIDTest() {
        packetBuilder.setUserID(null);
        packetBuilder.build();
    }
}
