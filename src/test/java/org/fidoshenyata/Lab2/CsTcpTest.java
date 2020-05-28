package org.fidoshenyata.Lab2;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.Lab2.CS.ClientTCP;
import org.fidoshenyata.Lab2.CS.ServerTCP;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class CsTcpTest {

    private Packet packet;
    private Packet packet2Magic;

    @BeforeClass
    public static void beforeClass(){
        (new Thread(() -> ServerTCP.main(null))).start();
    }

    @Before
    public void setUp() throws InterruptedException {
        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 5)
                .packetID(UnsignedLong.valueOf(2))
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(Message.CommandTypes.ADD_PRODUCT.ordinal())
                                .message("Hello From Client!")
                                .build()
                );
        packet = packetBuilder.build();

        packetBuilder.packetID(UnsignedLong.valueOf(0x13));
        packet2Magic = packetBuilder.build();

        Thread.sleep(200); // wait until sever is up
    }

    @Test
    public void TestOneThread() throws Exception {

        final int packetsInThread = 50;
        long succeedPackets = 0;

        ClientTCP clientTCP = new ClientTCP();
        clientTCP.connect(ServerTCP.PORT);

        for (int i = 0; i < packetsInThread; i++) {
            Packet response = clientTCP.request(packet);
            String message = response.getUsefulMessage().getMessage();
            assertEquals(message, "Ok");
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
                    ClientTCP clientTCP = new ClientTCP();
                    clientTCP.connect(ServerTCP.PORT);

                    for (int i = 0; i < packetsInThread; i++) {
                        Packet response = clientTCP.request(packet);
                        String message = response.getUsefulMessage().getMessage();
                        assertEquals(message, "Ok");
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
    public void test2MagicAndHalfPacket() throws Exception {
        ClientTCP client = new ClientTCP();
        client.connect(ServerTCP.PORT);

        Packet response = client.request(packet);
        String message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("Ok", message);

        response = client.request(packet2Magic);
        message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("Ok", message);

//        response = client.requestGivingHalfTEST(packet2Magic);
//        message = response.getUsefulMessage().getMessage();
//        Assert.assertEquals("Corrupt message was given", message);

        response = client.request(packet);
        message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("Ok", message);
    }
}