package org.fidoshenyata.Lab2;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.Lab2.CS.Client;
import org.fidoshenyata.Lab2.CS.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ClientServerTest {

    private Packet packet;
    @BeforeClass
    public static void beforeClass(){
        (new Thread(() -> Server.main(null))).start();
    }
    @Before
    public void setUp() {
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
    }

    @Test
    public void TestOneThread() throws IOException {

        final int packetsInThread = 50;
        long succeedPackets = 0;

        Client client = new Client();
        client.connect(Server.PORT);

        for (int i = 0; i < packetsInThread; i++) {
            Packet response = client.request(packet);
            String message = response.getUsefulMessage().getMessage();
            assertEquals(message, "Ok");
            succeedPackets++;
        }

        client.disconnect();

        Assert.assertEquals(packetsInThread, succeedPackets);
    }

    @Test
    public void TestMoreThreadsThanServer() throws InterruptedException {

        final int threads = Server.THREADS * 2;
        final int packetsInThread = 50;

        AtomicInteger succeedPackets = new AtomicInteger(0);
        long expectedSucceedPackets = threads * packetsInThread;

        Thread[] threadsArray = new Thread[threads];
        for (int k = 0; k < threads; k++) {
            threadsArray[k] = new Thread(() -> {
                try {
                    Client client = new Client();
                    client.connect(Server.PORT);

                    for (int i = 0; i < packetsInThread; i++) {
                        Packet response = client.request(packet);
                        String message = response.getUsefulMessage().getMessage();
                        assertEquals(message, "Ok");
                        succeedPackets.incrementAndGet();
                    }

                    client.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        for (int k = 0; k < threads; k++) {
            threadsArray[k].run();
        }

        for (int k = 0; k < threads; k++) {
            threadsArray[k].join();
        }

        Assert.assertEquals(expectedSucceedPackets, succeedPackets.longValue());
    }
}