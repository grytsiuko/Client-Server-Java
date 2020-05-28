package org.fidoshenyata.Lab3;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.exceptions.communication.NoAnswerException;
import org.fidoshenyata.lab3.CS.ClientUDP;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

    @Test(expected = NoAnswerException.class)
    public void testOneMessage() throws Exception {
        ClientUDP client = new ClientUDP();
        client.connect();
        client.request(packet);
    }

    @Test(expected = NoAnswerException.class)
    public void sendMultipleMessages() throws Exception {
        ClientUDP client = new ClientUDP();
        client.connect();

        for (int i = 0; i < 10; i++) {
            client.request(packet);
        }
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
                try {
                    ClientUDP client = new ClientUDP();
                    client.connect();
                    for (int i = 0; i < packetsInThread; i++) {
                        try {
                            client.request(packet);
                        } catch (NoAnswerException e) {
                            Assert.assertEquals(NoAnswerException.class, e.getClass());
                            succeedExceptions.incrementAndGet();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
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
