package org.fidoshenyata.network;

import org.fidoshenyata.network.utils.PacketDestinationInfo;
import org.fidoshenyata.packet.PacketCoder;
import org.fidoshenyata.packet.Packet;
import org.fidoshenyata.exceptions.cryption.DecryptionException;
import org.fidoshenyata.exceptions.cryption.EncryptionException;
import org.fidoshenyata.exceptions.cryption.KeyInitializationException;
import org.fidoshenyata.exceptions.cryption.TooLongMessageException;
import org.fidoshenyata.exceptions.packet.CorruptedPacketException;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.Arrays;

public class NetworkUDP {

    private final DatagramSocket socket;
    private byte[] buf;
    private PacketCoder packetCoder;

    private static final int BUF_LENGTH = Packet.MAX_PACKET_LENGTH;

    public NetworkUDP(DatagramSocket socket) throws KeyInitializationException {
        this.socket = socket;
        try {
            packetCoder = new PacketCoder(generateKey());
        } catch (InvalidKeyException e) {
            throw new KeyInitializationException();
        }
    }

    public PacketDestinationInfo receiveMessage()
            throws IOException, DecryptionException, CorruptedPacketException {

        buf = new byte[BUF_LENGTH];
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
        socket.receive(datagramPacket);

        Packet packet = packetCoder.decode(datagramPacket.getData());
        return new PacketDestinationInfo(packet,
                datagramPacket.getAddress(), datagramPacket.getPort());
    }

    public void sendMessage(PacketDestinationInfo packetDI)
            throws IOException, EncryptionException, TooLongMessageException {
        buf = packetCoder.encode(packetDI.getPacket());
        DatagramPacket packetUpd =
                new DatagramPacket(buf, buf.length, packetDI.getAddress(), packetDI.getPort());
        socket.send(packetUpd);
    }

    public void sendMessageHalfTEST(PacketDestinationInfo packetDI)
            throws IOException, EncryptionException, TooLongMessageException {
        byte[] fullPacket = packetCoder.encode(packetDI.getPacket());
        buf = Arrays.copyOf(fullPacket, fullPacket.length / 2);
        DatagramPacket packetUpd =
                new DatagramPacket(buf, buf.length, packetDI.getAddress(), packetDI.getPort());
        socket.send(packetUpd);
    }

    public void close() {
        socket.close();
    }

    private SecretKeySpec generateKey() {
        byte[] keyBytes = "verysecretsecretkey".getBytes(StandardCharsets.UTF_8);
        keyBytes = Arrays.copyOf(keyBytes, 16);
        return new SecretKeySpec(keyBytes, "AES");
    }
}
