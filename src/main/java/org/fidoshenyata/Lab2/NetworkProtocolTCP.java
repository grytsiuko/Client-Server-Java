package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.PacketCoder;
import org.fidoshenyata.Lab1.model.Packet;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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
        boolean packetIncomplete = true;
        int state = 0;
        int wLen = 0;

        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        ByteArrayOutputStream packetBytes = new ByteArrayOutputStream();

        byte oneByte[] = new byte[1];

        while (packetIncomplete && (inputStream.read(oneByte)) != -1) {
            if (PacketCoder.MAGIC_NUMBER == oneByte[0]) {
                state = 0;
                byteBuffer = ByteBuffer.allocate(10);
                packetBytes.reset();
            } else {
                byteBuffer.put(oneByte);
                switch (state) {
                    case 0:
                        if (!byteBuffer.hasRemaining()) {
                            byteBuffer = ByteBuffer.allocate(Integer.BYTES);
                            state = 1;
                        }
                        break;

                    case 1:
                        if (!byteBuffer.hasRemaining()) {
                            wLen = byteBuffer.getInt(0);
                            byteBuffer = ByteBuffer.allocate(Short.BYTES + 8 + wLen + Short.BYTES);
                            state = 2;
                        }
                        break;

                    case 2:
                        if (!byteBuffer.hasRemaining()) {
                            packetIncomplete = false;
                        }
                        break;
                }
            }
            packetBytes.write(oneByte);
        }

        byte[] fullPacket = packetBytes.toByteArray();
        return packetCoder.decode(fullPacket);
    }

    @Override
    public void sendMessage(Packet packet, OutputStream outputStream) throws Exception {
        byte[] packetBytes = packetCoder.encode(packet);
        outputStream.write(packetBytes);
        outputStream.flush();
    }

    public NetworkProtocolTCP() throws Exception {

        byte[] keyBytes = "verysecretsecretkey".getBytes("UTF-8");
        keyBytes = Arrays.copyOf(keyBytes, 16);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        packetCoder = new PacketCoder(keySpec);
    }
}
