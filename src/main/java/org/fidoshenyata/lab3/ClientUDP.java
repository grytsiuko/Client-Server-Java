package org.fidoshenyata.lab3;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

import java.io.IOException;
import java.net.*;

public class ClientUDP {

    private DatagramSocket socket;
    private InetAddress address;
    private NetworkUDP network;

    private int retryCount;

    private static final int PORT = 4445;
    private static final int RESEND_TIMEOUT = 1000;
    private static final int TIMEOUT_BETWEEN_RESEND = 2000;
    private static final int TIMES_RETRY = 2;

    public ClientUDP() {
    }

    public void connect() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        socket.setSoTimeout(RESEND_TIMEOUT);
        address = InetAddress.getLocalHost();
        network = new NetworkUDP(socket);
    }

    public Packet request(Packet packet) throws InterruptedException {
        try {
            network.sendMessage(new PacketDestinationInfo(packet, address, PORT));
            Packet res = network.receiveMessage().getPacket();
            retryCount = 0;
            return res;
        } catch (IOException e) {
            if (retryCount < TIMES_RETRY) {
                retryCount++;
                System.out.println("retrying for the " + retryCount + " time");
                Thread.sleep(TIMEOUT_BETWEEN_RESEND);
                return request(packet);
            } else e.printStackTrace();
        }
        return null;
    }

    public void disconnect() {
        socket.close();
    }

    public static void main(String[] args) {
        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 19)
                .packetID(UnsignedLong.valueOf(2))
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(Message.CommandTypes.ADD_PRODUCT.ordinal())
                                .message("Hello From Client!")
                                .build()
                );
        Packet packet = packetBuilder.build();

        final int threads = 1;
        final int packetsInThread = 10;
        for (int k = 0; k < threads; k++) {
            new Thread(() -> {
                try {
                    ClientUDP client = new ClientUDP();
                    client.connect();
                    int succeed = 0;
                    for (int i = 0; i < packetsInThread; i++) {
                        Packet response = client.request(packet);
                        String message = response.getUsefulMessage().getMessage();
                        if (!message.equals("Ok"))
                            System.out.println("Wrong response: " + message);
                        else
                            succeed++;
                    }
                    System.out.println(succeed + " of " + packetsInThread + " are succeed");
                    client.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
