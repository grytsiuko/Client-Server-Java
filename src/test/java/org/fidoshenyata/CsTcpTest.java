package org.fidoshenyata;

import org.fidoshenyata.client.ClientCS;
import org.fidoshenyata.packet.Message;
import org.fidoshenyata.packet.Packet;
import org.fidoshenyata.client.ClientTCP;
import org.fidoshenyata.server.ServerTCP;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class CsTcpTest {

    private Message requestMessage;
    private Message requestMessage2Magic;

    @BeforeClass
    public static void beforeClass() {
        new Thread(new ServerTCP()).start();
    }

    @Before
    public void setUp() throws InterruptedException {
        Message.MessageBuilder messageBuilder = Message.builder()
                .userID(2048)
                .commandType(Message.PING)
                .message("Hello From Client!");

        requestMessage = messageBuilder.build();
        requestMessage2Magic = messageBuilder.userID(0x13).build();

        Thread.sleep(200); // wait until sever is up
    }

    @Test
    public void TestOneThread() throws Exception {

        final int packetsInThread = 50;
        long succeedPackets = 0;

        ClientCS clientTCP = new ClientTCP();
        clientTCP.connect();

        for (int i = 0; i < packetsInThread; i++) {
            Packet response = clientTCP.request(requestMessage);
            String message = response.getUsefulMessage().getMessage();
            assertEquals(message, "PONG");
            succeedPackets++;
        }

        clientTCP.disconnect();

        Assert.assertEquals(packetsInThread, succeedPackets);
    }

    @Test
    public void TestMoreThreadsThanServer() throws InterruptedException {

        final int threads = ServerTCP.THREADS * 2;
        final int packetsInThread = 50;

        AtomicInteger succeedPackets = new AtomicInteger(0);
        long expectedSucceedPackets = threads * packetsInThread;

        Thread[] threadsArray = new Thread[threads];
        for (int k = 0; k < threads; k++) {
            threadsArray[k] = new Thread(() -> {
                try {
                    ClientCS clientTCP = new ClientTCP();
                    clientTCP.connect();

                    for (int i = 0; i < packetsInThread; i++) {
                        Packet response = clientTCP.request(requestMessage);
                        String message = response.getUsefulMessage().getMessage();
                        assertEquals(message, "PONG");
                        succeedPackets.incrementAndGet();
                    }

                    clientTCP.disconnect();
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
    public void samePacketIDTest() throws Exception {

        final int packets = 50;

        ClientCS clientTCP = new ClientTCP();
        clientTCP.connect();

        for (int i = 0; i < packets; i++) {
            Packet response = clientTCP.request(requestMessage);
            assertEquals(response.getPacketID(), clientTCP.getPacketCount());
        }

        clientTCP.disconnect();
    }

    @Test
    public void test2MagicAndHalfPacket() throws Exception {
        ClientTCP client = new ClientTCP();
        client.connect();

        Packet response = client.request(requestMessage);
        String message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("PONG", message);

        response = client.request(requestMessage2Magic);
        message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("PONG", message);

        client.requestGivingHalfTEST(requestMessage2Magic);
        client.requestGivingLargeNumbersTEST();

        response = client.request(requestMessage);
        message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("PONG", message);
    }
}