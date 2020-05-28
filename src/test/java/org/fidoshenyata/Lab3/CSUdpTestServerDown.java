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
import java.util.concurrent.atomic.AtomicInteger;

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

    @Test(expected = SocketTimeoutException.class)
    public void testOneMessage() throws IOException {
        ClientUDP client = new ClientUDP();
        client.connect();
        Packet response = client.request(packet);
    }

    @Test
    public void sendMultipleMessages() throws IOException {
        ClientUDP client = new ClientUDP();
        client.connect();

        int caughtExceptions = 0;
        int numberOfPackets = 3;
        for (int i = 0; i < numberOfPackets; i++) {
            try {
                Packet response = client.request(packet);
            } catch (SocketTimeoutException e) {
                caughtExceptions++;
            }
        }

        Assert.assertEquals(numberOfPackets, caughtExceptions);
    }

    @Test
    public void sendMultipleMessagesConcurrently() throws InterruptedException {

        final int threads = 5;
        final int packetsInThread = 2;

        AtomicInteger succeedExceptions = new AtomicInteger(0);
        long expectedSucceedExceptions = threads * packetsInThread;

        Thread[] threadsArray = new Thread[threads];
        for (int k = 0; k < threads; k++) {
            threadsArray[k] = new Thread(() -> {
                ClientUDP client = new ClientUDP();
                client.connect();
                for (int i = 0; i < packetsInThread; i++) {
                    try {
                        client.request(packet);
                    } catch (IOException e) {
                        Assert.assertEquals(SocketTimeoutException.class, e.getClass());
                        succeedExceptions.incrementAndGet();
                    }
                }
            });
        }

        for (int k = 0; k < threads; k++) {
            threadsArray[k].start();
        }

        for (int k = 0; k < threads; k++) {
            threadsArray[k].join();
        }

        Assert.assertEquals(expectedSucceedExceptions, succeedExceptions.longValue());
    }
}
