package org.fidoshenyata.Lab2.CS;

import com.google.common.primitives.UnsignedLong;
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

    public ClientTCP() {
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

    public Packet request(Packet packet)
            throws EncryptionException, DecryptionException, CorruptedPacketException,
            ServerUnavailableException, FailedHandShake, RequestInterruptedException, TooLongMessageException {

        if (networkTCP == null) {
            throw new IllegalStateException("Not connected yet");
        }

        try {
            networkTCP.sendMessage(packet);
            return networkTCP.receiveMessage();
        } catch (IOException | SocketClosedException e) {
            tryToConnect();
            throw new RequestInterruptedException();
        }
    }

    public void requestGivingHalfTEST(Packet packet)
            throws IOException, EncryptionException, TooLongMessageException {

        networkTCP.sendMessageHalfTEST(packet);
    }

    public void requestGivingLargeNumbersTEST()
            throws IOException {

        networkTCP.sendMessageLargeNumbersTEST();
    }

    public void disconnect() throws IOException {
        networkTCP.closeStreams();
        socket.close();
    }

    public static void main(String[] args) {
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
        Packet packet = packetBuilder.build();

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
                            Packet response = clientTCP.request(packet);
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
