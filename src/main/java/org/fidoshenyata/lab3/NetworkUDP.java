package org.fidoshenyata.lab3;

import org.fidoshenyata.Lab1.PacketCoder;
import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.exceptions.InvalidCRC16_1_Exception;
import org.fidoshenyata.exceptions.InvalidCRC16_2_Exception;
import org.fidoshenyata.exceptions.InvalidMagicByteException;

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

    public NetworkUDP(DatagramSocket socket) {
        this.socket = socket;
        try{
            packetCoder = new PacketCoder(generateKey());
        }catch(InvalidKeyException e){
            e.printStackTrace();
        }
    }

    public PacketDestinationInfo receiveMessage() throws IOException {
        buf = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
        socket.receive(datagramPacket);
        try {
            Packet packet = packetCoder.decode(datagramPacket.getData());
            return new PacketDestinationInfo(packet,
                    datagramPacket.getAddress(), datagramPacket.getPort());
        } catch (InvalidCRC16_1_Exception | InvalidMagicByteException | InvalidCRC16_2_Exception e) {
            e.printStackTrace();
        }
        return new PacketDestinationInfo(null,
                datagramPacket.getAddress(), datagramPacket.getPort());
    }

    public void sendMessage(PacketDestinationInfo packetDI) throws IOException {
        buf = packetCoder.encode(packetDI.getPacket());
        DatagramPacket packetUpd =
                new DatagramPacket(buf,buf.length, packetDI.getAddress(), packetDI.getPort());
        socket.send(packetUpd);
    }

    public void close(){
        socket.close();
    }

    private SecretKeySpec generateKey(){
        byte[] keyBytes = "verysecretsecretkey".getBytes(StandardCharsets.UTF_8);
        keyBytes = Arrays.copyOf(keyBytes, 16);
        return new SecretKeySpec(keyBytes, "AES");
    }
}
