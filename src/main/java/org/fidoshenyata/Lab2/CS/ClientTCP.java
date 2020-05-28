package org.fidoshenyata.Lab2.CS;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.Lab2.Network.NetworkTCP;
import org.fidoshenyata.exceptions.communication.SocketClosedException;
import org.fidoshenyata.exceptions.cryption.DecryptionException;
import org.fidoshenyata.exceptions.cryption.EncryptionException;
import org.fidoshenyata.exceptions.cryption.FailedHandShake;
import org.fidoshenyata.exceptions.packet.CorruptedPacketException;

import java.io.IOException;
import java.net.Socket;

public class ClientTCP {

    private Socket socket;
    private NetworkTCP networkTCP;

    public ClientTCP() {
    }

    public void connect(int serverPort) throws IOException, FailedHandShake {
        socket = new Socket("localhost", serverPort);
        networkTCP = new NetworkTCP(socket);
    }

    public Packet request(Packet packet)
            throws IOException, EncryptionException, SocketClosedException,
            DecryptionException, CorruptedPacketException {

        if (networkTCP == null) {
            throw new IllegalStateException("Not connected yet");
        }

        networkTCP.sendMessage(packet);
        return networkTCP.receiveMessage();
    }

    public Packet requestGivingHalfTEST(Packet packet)
            throws IOException, EncryptionException, SocketClosedException,
            DecryptionException, CorruptedPacketException {

        networkTCP.sendMessageHalfTEST(packet);
        return networkTCP.receiveMessage();
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

        final int threads = 2;
        final int packetsInThread = 50;

        for (int k = 0; k < threads; k++) {
            new Thread(() -> {
                try {
                    ClientTCP clientTCP = new ClientTCP();
                    clientTCP.connect(ServerTCP.PORT);

                    int succeed = 0;
                    for (int i = 0; i < packetsInThread; i++) {
                        Packet response = clientTCP.request(packet);
                        String message = response.getUsefulMessage().getMessage();
                        if (!message.equals("Ok"))
                            System.out.println("Wrong response: " + message);
                        else
                            succeed++;
                    }

                    System.out.println(succeed + " of " + packetsInThread + " are succeed");
                    clientTCP.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
