package org.fidoshenyata.Lab3;

import org.fidoshenyata.packet.Message;
import org.fidoshenyata.packet.Packet;
import org.fidoshenyata.client.ClientUDP;
import org.fidoshenyata.server.ServerUDP;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class CsUdpTest {

    private Message requestMessage;
    private Message requestMessage2Magic;

    @BeforeClass
    public static void beforeClass() {
        (new Thread(() -> {
            try {
                ServerUDP.main(new String[]{"OK"});
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();
    }

    @Before
    public void setUp() throws Exception {
        Message.MessageBuilder messageBuilder = Message.builder()
                .userID(2048)
                .commandType(Message.CommandTypes.ADD_PRODUCT.ordinal())
                .message("Hello From Client!");

        requestMessage = messageBuilder.build();
        requestMessage2Magic = messageBuilder.userID(0x13).build();

        Thread.sleep(200); // wait until sever is up
    }

    @Test
    public void sendOneMessage() throws Exception {
        ClientUDP client = new ClientUDP();
        client.connect();
        Packet response = client.request(requestMessage);
        String message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("Ok", message);
    }

    @Test
    public void sendMultipleMessages() throws Exception {
        ClientUDP client = new ClientUDP();
        client.connect();
        for (int i = 0; i < 10; i++) {
            Packet response = client.request(requestMessage);
            String message = response.getUsefulMessage().getMessage();
            Assert.assertEquals("Ok", message);
        }
    }

    @Test
    public void sendMultipleMessagesConcurrently() throws Exception {


        final int threads = 10;
        final int packetsInThread = 5;

        AtomicInteger succeedPackets = new AtomicInteger(0);
        long expectedSucceedPackets = threads * packetsInThread;

        Thread[] threadsArray = new Thread[threads];
        for (int k = 0; k < threads; k++) {
            threadsArray[k] = new Thread(() -> {
                try {
                    ClientUDP client = new ClientUDP();
                    client.connect();
                    for (int i = 0; i < packetsInThread; i++) {
                        Packet response = client.request(requestMessage);
                        String message = response.getUsefulMessage().getMessage();
                        Assert.assertEquals("Ok", message);
                        succeedPackets.incrementAndGet();
                    }
                } catch (Exception e) {
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

        Assert.assertEquals(expectedSucceedPackets, succeedPackets.longValue());
    }

    @Test
    public void samePacketId() throws Exception {

        final int packets = 50;

        ClientUDP client = new ClientUDP();
        client.connect();

        for (int i = 0; i < packets; i++) {
            Packet response = client.request(requestMessage);
            Assert.assertEquals(response.getPacketID(), client.getPacketCount());
        }

        client.disconnect();
    }

    @Test
    public void test2MagicAndHalfPacket() throws Exception {
        ClientUDP client = new ClientUDP();
        client.connect();

        Packet response = client.request(requestMessage);
        String message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("Ok", message);

        response = client.request(requestMessage2Magic);
        message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("Ok", message);

        client.requestGivingHalfTEST(requestMessage2Magic);

        response = client.request(requestMessage);
        message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("Ok", message);
    }
}
