package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.PacketCoder;
import org.fidoshenyata.Lab1.model.Packet;

import javax.crypto.KeyGenerator;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class NetworkProtocolTCP implements NetworkProtocol {

    public static Key KEY;

    static {
        try {
            KEY = KeyGenerator.getInstance("AES").generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private PacketCoder packetCoder;

    @Override
    public Packet receiveMessage(InputStream inputStream) throws Exception {
        byte[] inputBytes = inputStream.readAllBytes();
        return packetCoder.decode(inputBytes);
    }

    @Override
    public void sendMessage(Packet packet, OutputStream outputStream) throws Exception {
        byte[] packetBytes = packetCoder.encode(packet);
        outputStream.write(packetBytes);
        outputStream.flush();
    }

    public NetworkProtocolTCP() throws Exception {
        packetCoder = new PacketCoder(KEY);
    }
}
