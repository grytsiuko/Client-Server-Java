package org.fidoshenyata;

import com.github.snksoft.crc.CRC;
import org.fidoshenyata.model.Packet;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class PacketEncoder {

    private final static byte MAGIC_NUMBER = 0x13;
    private final static CRC CRC_INSTANCE = new CRC(CRC.Parameters.CRC16);

    private Key key;
    private Cipher cipher;
    private Packet packet;


    public PacketEncoder() {
    }

    public PacketEncoder setPacket(Packet packet) {
        this.packet = packet;
        return this;
    }

    public PacketEncoder setKey(Key key) {
        this.key = key;
        return this;
    }

    public PacketEncoder setAlgorithm(String algorithm)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance(algorithm);
        return this;
    }

    public byte[] encode() throws Exception {
        if (this.key == null) {
            throw new IllegalStateException("Key is not defined");
        }
        if (this.cipher == null) {
            throw new IllegalStateException("Cipher and Algorithm are not defined");
        }
        if (this.packet == null) {
            throw new IllegalStateException("Packet is not defined");
        }
        return encodeLegalState();
    }

    private byte[] encodeLegalState() throws Exception {
        byte[] messageEncryptedBytes = getEncryptedMessage();
        ByteBuffer byteBuffer = ByteBuffer.allocate(26 + messageEncryptedBytes.length);

        putMetadata(byteBuffer, messageEncryptedBytes);
        putMessageBlock(byteBuffer, messageEncryptedBytes);

        return byteBuffer.array();
    }

    private byte[] getEncryptedMessage() throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, this.key);
        byte[] messageBytes = this.packet.getMessage().getBytes(StandardCharsets.UTF_8);
        return cipher.doFinal(messageBytes);
    }

    private void putMetadata(ByteBuffer byteBuffer, byte[] messageEncryptedBytes) throws Exception {
        byteBuffer.put(MAGIC_NUMBER);
        byteBuffer.put(this.packet.getSource());
        byteBuffer.putLong(this.packet.getPacketID());
        byteBuffer.putInt(messageEncryptedBytes.length + 8);

        byte[] metadata = new byte[14];
        byteBuffer.position(0).get(metadata);
        short metadataCRC = (short) CRC_INSTANCE.calculateCRC(metadata);
        byteBuffer.putShort(metadataCRC);
    }

    private void putMessageBlock(ByteBuffer byteBuffer, byte[] messageEncryptedBytes) throws Exception {
        byteBuffer.putInt(this.packet.getCommandType());
        byteBuffer.putInt(this.packet.getUserID());
        byteBuffer.put(messageEncryptedBytes);

        byte[] messageBlock = new byte[messageEncryptedBytes.length + 8];
        byteBuffer.position(16).get(messageBlock);
        short messageBlockCRC = (short) CRC_INSTANCE.calculateCRC(messageBlock);
        byteBuffer.putShort(messageBlockCRC);
    }
}
