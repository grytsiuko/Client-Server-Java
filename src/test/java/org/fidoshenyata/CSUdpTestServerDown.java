package org.fidoshenyata;

import org.fidoshenyata.client.ClientCS;
import org.fidoshenyata.packet.Message;
import org.fidoshenyata.exceptions.communication.NoAnswerException;
import org.fidoshenyata.client.ClientUDP;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class CSUdpTestServerDown {
    private Message requestMessage;

    @Before
    public void setUp() {
        Message.MessageBuilder messageBuilder = Message.builder()
                .userID(2048)
                .commandType(Message.CommandTypes.ADD_PRODUCT.ordinal())
                .message("Hello From Client!");

        requestMessage = messageBuilder.build();
    }

    @Test(expected = NoAnswerException.class)
    public void testOneMessage() throws Exception {
        ClientCS client = new ClientUDP();
        client.connect();
        client.request(requestMessage);
    }

    @Test(expected = NoAnswerException.class)
    public void sendMultipleMessages() throws Exception {
        ClientCS client = new ClientUDP();
        client.connect();

        for (int i = 0; i < 10; i++) {
            client.request(requestMessage);
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
                    ClientCS client = new ClientUDP();
                    client.connect();
                    for (int i = 0; i < packetsInThread; i++) {
                        try {
                            client.request(requestMessage);
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
