package org.fidoshenyata.client;

import com.google.common.primitives.UnsignedLong;
import lombok.Getter;
import org.fidoshenyata.packet.Message;
import org.fidoshenyata.packet.Packet;
import org.fidoshenyata.exceptions.communication.NoAnswerException;
import org.fidoshenyata.exceptions.cryption.DecryptionException;
import org.fidoshenyata.exceptions.cryption.EncryptionException;
import org.fidoshenyata.exceptions.cryption.KeyInitializationException;
import org.fidoshenyata.exceptions.cryption.TooLongMessageException;
import org.fidoshenyata.exceptions.packet.CorruptedPacketException;
import org.fidoshenyata.network.NetworkUDP;
import org.fidoshenyata.network.utils.PacketDestinationInfo;

import java.io.IOException;
import java.net.*;

public class ClientUDP implements ClientCS {

    private DatagramSocket socket;
    private InetAddress address;
    private NetworkUDP network;
    private Packet.PacketBuilder packetBuilder;

    @Getter
    private UnsignedLong packetCount;

    private static final int PORT = 4445;
    private static final int RESEND_TIMEOUT = 500;
    private static final int TIMEOUT_BETWEEN_RESEND = 1000;
    private static final int TIMES_RETRY = 3;

    public ClientUDP() {
        packetCount = UnsignedLong.valueOf(0);
        packetBuilder = Packet
                .builder()
                .source((byte) 1);
    }

    @Override
    public void connect() throws KeyInitializationException, SocketException, UnknownHostException {
        socket = new DatagramSocket();
        socket.setSoTimeout(RESEND_TIMEOUT);
        address = InetAddress.getLocalHost();
        network = new NetworkUDP(socket);
    }

    @Override
    public Packet request(Message message)
            throws EncryptionException, DecryptionException, CorruptedPacketException,
            NoAnswerException, TooLongMessageException {

        incrementPacketCount();
        int resendCounter = 0;

        while (resendCounter < TIMES_RETRY) {
            try {
                packetBuilder
                        .usefulMessage(message)
                        .packetID(packetCount);
                network.sendMessage(new PacketDestinationInfo(packetBuilder.build(), address, PORT));
                return network.receiveMessage().getPacket();
            } catch (IOException e) {
                resendCounter++;
                System.out.println("Retrying sending for the " + resendCounter + " time");
            }

            try {
                Thread.sleep(TIMEOUT_BETWEEN_RESEND);
            } catch (InterruptedException e) {
                System.out.println("Interrupted while retrying");
            }
        }

        throw new NoAnswerException();
    }

    @Override
    public void requestGivingHalfTEST(Message message)
            throws IOException, EncryptionException, TooLongMessageException {

        incrementPacketCount();
        packetBuilder
                .usefulMessage(message)
                .packetID(packetCount);
        network.sendMessageHalfTEST(new PacketDestinationInfo(packetBuilder.build(), address, PORT));
    }

    @Override
    public void disconnect() {
        socket.close();
    }

    private void incrementPacketCount() {
        packetCount = packetCount.plus(UnsignedLong.valueOf(1));
    }

    public static void main(String[] args) {
        final int threads = 10;
        final int packetsInThread = 10;

        Message requestMessage = Message.builder()
                .userID(2048)
                .commandType(Message.COMMAND_ADD_PRODUCT)
                .message("Hello From Client!")
                .build();

        for (int k = 0; k < threads; k++) {
            new Thread(() -> {
                try {
                    ClientUDP client = new ClientUDP();
                    client.connect();
                    int succeed = 0;
                    for (int i = 0; i < packetsInThread; i++) {
                        Packet response = client.request(requestMessage);
                        String message = response.getUsefulMessage().getMessage();

                        if (!message.equals("Ok")) System.out.println("Wrong response: " + message);
                        else succeed++;
                    }
                    System.out.println(succeed + " of " + packetsInThread + " are succeed");
                    client.disconnect();
                } catch (DecryptionException e) {
                    System.out.println("Decryption error occurred");
                } catch (EncryptionException e) {
                    System.out.println("Encryption error occurred");
                } catch (SocketException e) {
                    System.out.println("Socket error occurred");
                } catch (UnknownHostException e) {
                    System.out.println("Unknown host");
                } catch (CorruptedPacketException e) {
                    System.out.println("Packet was corrupted");
                } catch (KeyInitializationException e) {
                    System.out.println("Illegal key initialization");
                } catch (NoAnswerException e) {
                    System.out.println("No answer from server");
                } catch (TooLongMessageException e) {
                    System.out.println("Too long message");
                }
            }).start();
        }
    }
}
