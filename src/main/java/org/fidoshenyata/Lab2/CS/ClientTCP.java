package org.fidoshenyata.Lab2.CS;

import com.google.common.primitives.UnsignedLong;
import lombok.Getter;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.Lab2.Network.NetworkTCP;
import org.fidoshenyata.exceptions.communication.RequestInterruptedException;
import org.fidoshenyata.exceptions.communication.ServerUnavailableException;
import org.fidoshenyata.exceptions.communication.SocketClosedException;
import org.fidoshenyata.exceptions.cryption.DecryptionException;
import org.fidoshenyata.exceptions.cryption.EncryptionException;
import org.fidoshenyata.exceptions.cryption.FailedHandShake;
import org.fidoshenyata.exceptions.cryption.TooLongMessageException;
import org.fidoshenyata.exceptions.packet.CorruptedPacketException;

import java.io.IOException;
import java.net.Socket;

public class ClientTCP {

    private final static int RECONNECTION_TRIES = 5;
    private final static int RECONNECTION_DELAY = 3000;

    private Socket socket;
    private NetworkTCP networkTCP;
    private int serverPort;
    private Packet.PacketBuilder packetBuilder;

    @Getter
    private UnsignedLong packetCount;

    public ClientTCP() {
        packetCount = UnsignedLong.valueOf(0);
        packetBuilder = Packet
                .builder()
                .source((byte) 1);
    }

    public void connect(int serverPort) throws FailedHandShake, ServerUnavailableException {
        this.serverPort = serverPort;
        tryToConnect();
    }

    private void tryToConnect() throws FailedHandShake, ServerUnavailableException {
        for (int i = 1; i <= RECONNECTION_TRIES; i++) {
            try {
                socket = new Socket("localhost", serverPort);
                networkTCP = new NetworkTCP(socket);
                return;
            } catch (IOException e) {
                System.out.println(i + " try to connect was unsuccessful");
                if (i == RECONNECTION_TRIES)
                    throw new ServerUnavailableException();
                try {
                    Thread.sleep(RECONNECTION_DELAY);
                } catch (InterruptedException e1) {
                    throw new ServerUnavailableException();
                }
            }
        }
    }

    public Packet request(Message message)
            throws EncryptionException, DecryptionException, CorruptedPacketException,
            ServerUnavailableException, FailedHandShake, RequestInterruptedException, TooLongMessageException {

        incrementPacketCount();

        try {
            packetBuilder
                    .usefulMessage(message)
                    .packetID(packetCount);
            networkTCP.sendMessage(packetBuilder.build());
            return networkTCP.receiveMessage();
        } catch (IOException | SocketClosedException e) {
            tryToConnect();
            throw new RequestInterruptedException();
        }
    }

    public void requestGivingHalfTEST(Message message)
            throws IOException, EncryptionException, TooLongMessageException {

        incrementPacketCount();
        packetBuilder
                .usefulMessage(message)
                .packetID(packetCount);
        networkTCP.sendMessageHalfTEST(packetBuilder.build());

    }

    public void requestGivingLargeNumbersTEST()
            throws IOException {

        networkTCP.sendMessageLargeNumbersTEST();
    }

    public void disconnect() throws IOException {
        networkTCP.closeStreams();
        socket.close();
    }

    private void incrementPacketCount() {
        packetCount = packetCount.plus(UnsignedLong.valueOf(1));
    }

    public static void main(String[] args) {

        Message requestMessage = Message.builder()
                .userID(2048)
                .commandType(Message.CommandTypes.ADD_PRODUCT.ordinal())
                .message("Hello From Client!")
                .build();


        final int threads = 5;
        final int packetsInThread = 10;

        for (int k = 0; k < threads; k++) {
            new Thread(() -> {
                try {
                    ClientTCP clientTCP = new ClientTCP();
                    clientTCP.connect(ServerTCP.PORT);

                    int succeed = 0;
                    for (int i = 0; i < packetsInThread; i++) {
                        try {

                            System.out.println("Client sent");
                            Packet response = clientTCP.request(requestMessage);
                            String message = response.getUsefulMessage().getMessage();
                            System.out.println("Client received");
                            if (!message.equals("Ok"))
                                System.out.println("Wrong response: " + message);
                            else
                                succeed++;

                        } catch (RequestInterruptedException e) {
                            System.out.println("Connection reestablished");
                        } catch (TooLongMessageException e) {
                            System.out.println("Too long message");
                        }

                        // for testing server turning off and on
//                        try {
//                            Thread.sleep(3000);
//                        } catch (InterruptedException e) {
//                            System.out.println("Thread was interrupted");
//                        }
                    }

                    System.out.println(succeed + " of " + packetsInThread + " are succeed");
                    clientTCP.disconnect();
                } catch (DecryptionException e) {
                    System.out.println("Decryption error occurred");
                } catch (FailedHandShake e) {
                    System.out.println("HandShake failed");
                } catch (EncryptionException e) {
                    System.out.println("Encryption error occurred");
                } catch (IOException e) {
                    System.out.println("IO error occurred, server might be down");
                } catch (CorruptedPacketException e) {
                    System.out.println("Corrupted packet received");
                } catch (ServerUnavailableException e) {
                    System.out.println("Server is currently unavailable");
                }
            }).start();
        }
    }

}
