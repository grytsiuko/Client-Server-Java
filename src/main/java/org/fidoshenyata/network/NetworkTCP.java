package org.fidoshenyata.network;

import org.fidoshenyata.network.utils.Keys;
import org.fidoshenyata.packet.PacketCoder;
import org.fidoshenyata.packet.Message;
import org.fidoshenyata.packet.Packet;
import org.fidoshenyata.exceptions.communication.SocketClosedException;
import org.fidoshenyata.exceptions.cryption.DecryptionException;
import org.fidoshenyata.exceptions.cryption.EncryptionException;
import org.fidoshenyata.exceptions.cryption.FailedHandShake;
import org.fidoshenyata.exceptions.cryption.TooLongMessageException;
import org.fidoshenyata.exceptions.packet.CorruptedPacketException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Arrays;

public class NetworkTCP {

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private PacketCoder packetCoder;

    public NetworkTCP(Socket socket) throws IOException, FailedHandShake {
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();

        try {
            Key key = new Keys().doHandShake(inputStream, outputStream);
            packetCoder = new PacketCoder(key);
        } catch (InvalidKeyException e) {
            throw new FailedHandShake();
        }
    }

    public Packet receiveMessage()
            throws IOException, SocketClosedException, CorruptedPacketException, DecryptionException {

        byte[] maxPacketBuffer = new byte[Packet.MAX_PACKET_LENGTH];

        if(inputStream.read(maxPacketBuffer) == -1){
            throw new SocketClosedException();
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(maxPacketBuffer);
        Integer messageLength = byteBuffer.getInt(Packet.POSITION_LENGTH);

        if (messageLength > Message.MAX_MESSAGE_LENGTH){
            throw new CorruptedPacketException();
        }

        byte[] finalPacket = new byte[Packet.LENGTH_ALL_WITHOUT_MESSAGE + messageLength];
        byteBuffer.position(0);
        byteBuffer.get(finalPacket);

        return packetCoder.decode(finalPacket);
    }

    public void sendMessage(Packet packet)
            throws IOException, EncryptionException, TooLongMessageException {
        byte[] packetBytes = packetCoder.encode(packet);
        outputStream.write(packetBytes);
        outputStream.flush();
    }

    public void sendMessageHalfTEST(Packet packet)
            throws IOException, EncryptionException, TooLongMessageException {
        byte[] packetBytes = packetCoder.encode(packet);
        byte[] packetPart = Arrays.copyOf(packetBytes, packetBytes.length / 2);
        outputStream.write(packetPart);
        outputStream.flush();
    }

    public void sendMessageLargeNumbersTEST() throws IOException {
        byte[] packetBytes = new byte[Packet.MAX_PACKET_LENGTH];
        for(int i = 0; i < packetBytes.length; i++){
            packetBytes[i] = 100; // wLen becomes large number - 4 bytes
        }
        outputStream.write(packetBytes);
        outputStream.flush();
    }

    public void closeStreams() throws IOException {
        inputStream.close();
        outputStream.close();
    }
}
