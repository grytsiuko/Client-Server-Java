package org.fidoshenyata.Lab3;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.lab3.ClientUDP;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class CSUdpTestServerDown {
    private Packet packet;

    @Before
    public void setUp() {
        Message.MessageBuilder messageBuilder = Message.builder()
                .userID(2048)
                .commandType(Message.CommandTypes.ADD_PRODUCT.ordinal())
                .message("Hello From Client!");
        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 5)
                .packetID(UnsignedLong.valueOf(2))
                .usefulMessage(messageBuilder.build());
        packet = packetBuilder.build();
    }

    @Test(expected = IOException.class)
    public void testOneMessage() throws IOException {
        ClientUDP client = new ClientUDP();
        client.connect();
        Packet response = client.request(packet);
    }

    @Test(expected = IOException.class)
    public void sendMultipleMessages() throws IOException {
        ClientUDP client = new ClientUDP();
        client.connect();
        for (int i = 0; i < 10; i++) {
            Packet response = client.request(packet);
        }
    }

    @Test
    public void sendMultipleMessagesConcurrently(){
        for (int k = 0; k < 10; k++) {
            new Thread(() -> {
                ClientUDP client = new ClientUDP();
                client.connect();
                for (int i = 0; i < 10; i++) {
                    try {
                        client.request(packet);
                    } catch (IOException e) {
                        Assert.assertEquals(SocketTimeoutException.class, e.getClass());
                    }
                }
            }).start();
        }
    }
}
