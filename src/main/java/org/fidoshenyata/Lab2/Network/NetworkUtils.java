package org.fidoshenyata.Lab2.Network;

import org.fidoshenyata.Lab1.PacketCoder;
import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.exceptions.InvalidCRC16_1_Exception;
import org.fidoshenyata.exceptions.InvalidCRC16_2_Exception;
import org.fidoshenyata.exceptions.InvalidMagicByteException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.security.InvalidKeyException;
import java.security.Key;

public class NetworkUtils {

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private PacketCoder packetCoder;

    public NetworkUtils(Socket socket) throws IOException {
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();

        Key key = new Keys().doHandShake(inputStream, outputStream);
        try {
            packetCoder = new PacketCoder(key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public Packet receiveMessage() throws IOException {
        boolean started = false;
        boolean packetIncomplete = true;
        int state = 0;
        int wLen;

        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        ByteArrayOutputStream packetBytes = new ByteArrayOutputStream();

        byte[] oneByte = new byte[1];

        while (packetIncomplete && (inputStream.read(oneByte)) != -1) {
            if (!started) {
                if (Packet.MAGIC_NUMBER == oneByte[0]) {
                    state = 0;
                    byteBuffer = ByteBuffer.allocate(
                            Packet.LENGTH_METADATA_WITHOUT_LENGTH - Packet.LENGTH_MAGIC_BYTE);
                    packetBytes.reset();
                    started = true;
                }
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
                            byteBuffer = ByteBuffer.allocate(
                                    Packet.LENGTH_ALL_WITHOUT_MESSAGE - Packet.LENGTH_METADATA + wLen);
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

        if(packetIncomplete){
            throw new ClosedChannelException();
        }
        byte[] fullPacket = packetBytes.toByteArray();
        try {
            return packetCoder.decode(fullPacket);
        } catch (InvalidCRC16_1_Exception | InvalidMagicByteException | InvalidCRC16_2_Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendMessage(Packet packet) throws IOException {
        byte[] packetBytes = packetCoder.encode(packet);
        outputStream.write(packetBytes);
        outputStream.flush();
    }

    public void closeStreams() throws IOException {
        inputStream.close();
        outputStream.close();
    }
}
