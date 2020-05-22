package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.PacketCoder;
import org.fidoshenyata.Lab1.model.Packet;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.Key;

public class NetworkUtils {

    private PacketCoder packetCoder;

    private InputStream inputStream;
    private OutputStream outputStream;

    public NetworkUtils(Socket socket) throws Exception {
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();

        Key key = new Keys().doHandShake(inputStream, outputStream);
        packetCoder = new PacketCoder(key);
    }

    public Packet receiveMessage() throws Exception {
        boolean packetIncomplete = true;
        int state = 0;
        int wLen = 0;

        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        ByteArrayOutputStream packetBytes = new ByteArrayOutputStream();

        byte oneByte[] = new byte[1];

        while (packetIncomplete && (inputStream.read(oneByte)) != -1) {
            if (Packet.MAGIC_NUMBER == oneByte[0]) {
                state = 0;
                byteBuffer = ByteBuffer.allocate(
                        Packet.LENGTH_METADATA_WITHOUT_LENGTH - Packet.LENGTH_MAGIC_BYTE);
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

        byte[] fullPacket = packetBytes.toByteArray();
        return packetCoder.decode(fullPacket);
    }

    public void sendMessage(Packet packet) throws Exception {
        byte[] packetBytes = packetCoder.encode(packet);
        outputStream.write(packetBytes);
        outputStream.flush();
    }

    public void closeStreams() throws Exception{
        inputStream.close();
        outputStream.close();
    }
}
