package org.fidoshenyata.lab3;

import com.google.common.primitives.UnsignedLong;
import lombok.Getter;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

import java.io.IOException;
import java.net.*;

public class ClientUDP {

    private DatagramSocket socket;
    private InetAddress address;
    private NetworkUDP network;

    @Getter
    private int packetCount;

    private static final int PORT = 4445;
    private static final int RESEND_TIMEOUT = 500;
    private static final int TIMEOUT_BETWEEN_RESEND = 1000;
    private static final int TIMES_RETRY = 3;

    public ClientUDP() {
    }

    public void connect(){
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(RESEND_TIMEOUT);
            address = InetAddress.getLocalHost();
            network = new NetworkUDP(socket);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public Packet request(Packet packet) throws IOException {

        int resendCounter = 0;
        while(resendCounter <TIMES_RETRY){
            try{
                network.sendMessage(new PacketDestinationInfo(packet, address, PORT));
                Packet res = network.receiveMessage().getPacket();
                return res;
            } catch(IOException e){
                resendCounter++;
                if(resendCounter == TIMES_RETRY) throw e;
                System.out.println("retrying for the " + resendCounter + " time");
            }
            try {
                Thread.sleep(TIMEOUT_BETWEEN_RESEND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Packet requestGivingHalfTEST(Packet packet) throws IOException {
        network.sendMessageHalfTEST(new PacketDestinationInfo(packet, address, PORT));
        Packet res = network.receiveMessage().getPacket();
        packetCount++;
        return res;
    }

    public void disconnect() {
        socket.close();
    }

    public static void main(String[] args) {
        final int threads = 10;
        final int packetsInThread = 10;
        for (int k = 0; k < threads; k++) {
            new Thread(() -> {
                try {
                    ClientUDP client = new ClientUDP();
                    client.connect();
                    int succeed = 0;
                    for (int i = 0; i < packetsInThread; i++) {
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

                        Packet packet = packetBuilder.build();
                        Packet response = client.request(packet);
                        String message = response.getUsefulMessage().getMessage();

                        if (!message.equals("Ok")) System.out.println("Wrong response: " + message);
                        else succeed++;
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
