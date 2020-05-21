package org.fidoshenyata.Lab1;

import com.github.snksoft.crc.CRC;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;

public class PacketCoder {
    public final static byte MAGIC_NUMBER = 0x13;

    private final static CRC CRC_INSTANCE = new CRC(CRC.Parameters.CRC16);
    private final static PacketBytesValidator packetBytesValidator = new PacketBytesValidator();
    private final Packet.PacketBuilder packetBuilder;
    private final Message.MessageBuilder messageBuilder;
    private final Cipher cipherEncrypt;
    private final Cipher cipherDecrypt;

    public PacketCoder(Key key) throws Exception {
        if (key == null) {
            throw new IllegalStateException("Key must be defined");
        }
        packetBuilder = Packet.builder();
        messageBuilder = Message.builder();

        cipherEncrypt = Cipher.getInstance("AES");
        cipherDecrypt = Cipher.getInstance("AES");

        cipherEncrypt.init(Cipher.ENCRYPT_MODE, key);
        cipherDecrypt.init(Cipher.DECRYPT_MODE, key);
    }

    public byte[] encode(Packet packet) throws Exception {
        if (packet == null) {
            throw new IllegalArgumentException("Packet is not defined");
        }
        return encodeLegalState(packet);
    }

    public Packet decode(byte[] packetArray) throws Exception {
        if (!packetBytesValidator.isValid(packetArray)) {
            throw new IllegalArgumentException("The packet is corrupt");
        }

        ByteBuffer buffer = ByteBuffer.wrap(packetArray);

        return packetBuilder
                .source(buffer.get(1))
                .packetID(buffer.getLong(2))
                .usefulMessage(
                        messageBuilder
                                .commandType(buffer.getInt(16))
                                .userID(buffer.getInt(20))
                                .message(getDecodedMessage(buffer))
                                .build()
                )
                .build();
    }

    private byte[] getEncryptedMessage(Packet packet) throws Exception {
        byte[] messageBytes = packet
                .getUsefulMessage().getMessage().getBytes(StandardCharsets.UTF_8);
        return cipherEncrypt.doFinal(messageBytes);
    }

    private String getDecodedMessage(ByteBuffer buffer) throws Exception {
        int messageLength = buffer.getInt(10);
        int metadataMessageLength = 8;
        byte[] message = new byte[messageLength - metadataMessageLength];
        buffer.position(24);
        buffer.get(message);
        return new String(cipherDecrypt.doFinal(message));
    }

    private byte[] encodeLegalState(Packet packet) throws Exception {
        byte[] messageEncryptedBytes = getEncryptedMessage(packet);
        ByteBuffer byteBuffer = ByteBuffer.allocate(26 + messageEncryptedBytes.length);

        putMetadata(byteBuffer, packet, messageEncryptedBytes);
        putMessageBlock(byteBuffer, packet, messageEncryptedBytes);

        return byteBuffer.array();
    }

    private void putMetadata(ByteBuffer byteBuffer, Packet packet, byte[] messageEncryptedBytes) {
        byteBuffer
                .put(MAGIC_NUMBER)
                .put(packet.getSource())
                .putLong(packet.getPacketID())
                .putInt(messageEncryptedBytes.length + 8);

        byte[] metadata = new byte[14];
        byteBuffer.position(0).get(metadata);
        short metadataCRC = (short) CRC_INSTANCE.calculateCRC(metadata);
        byteBuffer.putShort(metadataCRC);
    }

    private void putMessageBlock(ByteBuffer byteBuffer, Packet packet, byte[] messageEncryptedBytes) {
        byteBuffer
                .putInt(packet.getUsefulMessage().getCommandType())
                .putInt(packet.getUsefulMessage().getUserID())
                .put(messageEncryptedBytes);

        byte[] messageBlock = new byte[messageEncryptedBytes.length + 8];
        byteBuffer.position(16).get(messageBlock);
        short messageBlockCRC = (short) CRC_INSTANCE.calculateCRC(messageBlock);
        byteBuffer.putShort(messageBlockCRC);
    }

    private static class PacketBytesValidator {
        private ByteBuffer buffer;

        public PacketBytesValidator() {
        }

        public boolean isValid(byte[] packetArray) {
            buffer = ByteBuffer.wrap(packetArray);
            return isUncorruptedMetadata() && isUncorruptedMessage();
        }

        private boolean isUncorruptedMetadata() {
            byte[] metadata = new byte[14];
            buffer.get(metadata);
            short calculatedCRC = (short) CRC_INSTANCE.calculateCRC(metadata);
            short packetCRC = buffer.getShort();
            return packetCRC == calculatedCRC;
        }

        private boolean isUncorruptedMessage() {
            int messageLength = buffer.getInt(10);
            byte[] message = new byte[messageLength];
            buffer.position(16);
            buffer.get(message);
            short calculatedCRC = (short) CRC_INSTANCE.calculateCRC(message);
            short packetCRC = buffer.getShort();
            return packetCRC == calculatedCRC;
        }
    }
}
