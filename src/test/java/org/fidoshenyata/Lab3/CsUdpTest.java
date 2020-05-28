package org.fidoshenyata.Lab3;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.lab3.ClientUDP;
import org.fidoshenyata.lab3.ServerUDP;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class CsUdpTest {

    private Packet packet;
    private Packet packet2Magic;

    @BeforeClass
    public static void beforeClass(){
        (new Thread(() -> {
            try {
                ServerUDP.main(new String[]{"OK"});
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();
    }

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
        messageBuilder.userID(19);
        packetBuilder.usefulMessage(messageBuilder.build());
        packet2Magic = packetBuilder.build();
    }

    @Test
    public void sendOneMessage() throws IOException {
        ClientUDP client = new ClientUDP();
        client.connect();
        Packet response = client.request(packet);
        String message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("Ok", message);
    }

    @Test
    public void sendMultipleMessages() throws IOException {
        ClientUDP client = new ClientUDP();
        client.connect();
        for (int i = 0; i < 10; i++) {
            Packet response = client.request(packet);
            String message = response.getUsefulMessage().getMessage();
            Assert.assertEquals("Ok", message);
        }
    }

    @Test
    public void sendMultipleMessagesConcurrently() throws InterruptedException {


        final int threads = 20;
        final int packetsInThread = 50;

        AtomicInteger succeedPackets = new AtomicInteger(0);
        long expectedSucceedPackets = threads * packetsInThread;

        Thread[] threadsArray = new Thread[threads];
        for (int k = 0; k < threads; k++) {
            threadsArray[k] = new Thread(() -> {
                ClientUDP client = new ClientUDP();
                client.connect();
                for (int i = 0; i < packetsInThread; i++) {
                    Packet response = null;
                    try {
                        response = client.request(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String message = response.getUsefulMessage().getMessage();
                    Assert.assertEquals("Ok", message);
                    succeedPackets.incrementAndGet();
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
    public void samePacketId() throws IOException{
        ClientUDP client = new ClientUDP();
        client.connect();
        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 19)
                .packetID(UnsignedLong.valueOf(client.getPacketCount()))
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(Message.CommandTypes.ADD_PRODUCT.ordinal())
                                .message("Hello From Client!")
                                .build()
                );
        Packet sentPacket = packetBuilder.build();
        Packet response = client.request(sentPacket);
        Assert.assertEquals(sentPacket.getPacketID(), response.getPacketID());
    }

    @Test
    public void test2MagicAndHalfPacket() throws IOException {
        ClientUDP client = new ClientUDP();
        client.connect();
        Packet response = client.request(packet);
        String message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("Ok", message);
        response = client.request(packet2Magic);
        message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("Ok", message);
        response = client.requestGivingHalfTEST(packet2Magic);
        message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("Corrupt message was given", message);
        response = client.request(packet);
        message = response.getUsefulMessage().getMessage();
        Assert.assertEquals("Ok", message);
    }
}
