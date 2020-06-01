package org.fidoshenyata.packet;

import com.github.snksoft.crc.CRC;
import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.exceptions.cryption.DecryptionException;
import org.fidoshenyata.exceptions.cryption.EncryptionException;
import org.fidoshenyata.exceptions.cryption.TooLongMessageException;
import org.fidoshenyata.exceptions.packet.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class PacketCoder {

    private final CRC instanceCRC;
    private final PacketBytesValidator packetBytesValidator;
    private final Packet.PacketBuilder packetBuilder;
    private final Message.MessageBuilder messageBuilder;
    private Cipher cipherEncrypt;
    private Cipher cipherDecrypt;

    public PacketCoder(Key key) throws InvalidKeyException {
        if (key == null) {
            throw new IllegalStateException("Key must be defined");
        }
        packetBuilder = Packet.builder();
        messageBuilder = Message.builder();

        instanceCRC = new CRC(CRC.Parameters.CRC16);
        packetBytesValidator = new PacketBytesValidator();

        try {
            cipherEncrypt = Cipher.getInstance("AES");
            cipherDecrypt = Cipher.getInstance("AES");

            cipherEncrypt.init(Cipher.ENCRYPT_MODE, key);
            cipherDecrypt.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public Packet decode(byte[] packetArray) throws CorruptedPacketException, DecryptionException {
        packetBytesValidator.validate(packetArray);

        ByteBuffer buffer = ByteBuffer.wrap(packetArray);

        return packetBuilder
                .source(buffer.get(Packet.POSITION_SOURCE))
                .packetID(UnsignedLong.valueOf(buffer.getLong(Packet.POSITION_PACKET_ID)))
                .usefulMessage(
                        messageBuilder
                                .commandType(buffer.getInt(Packet.POSITION_COMMAND_TYPE))
                                .userID(buffer.getInt(Packet.POSITION_USER_ID))
                                .message(getDecodedMessage(buffer))
                                .build()
                )
                .build();
    }

    private String getDecodedMessage(ByteBuffer buffer) throws DecryptionException {
        int messageLength = buffer.getInt(Packet.POSITION_LENGTH);
        byte[] message = new byte[messageLength];
        buffer.position(Packet.POSITION_MESSAGE);
        buffer.get(message);
        try {
            return new String(cipherDecrypt.doFinal(message));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new DecryptionException();
        }
    }

    public byte[] encode(Packet packet) throws EncryptionException, TooLongMessageException {
        if (packet == null) {
            throw new IllegalArgumentException("Packet is not defined");
        }

        byte[] messageEncryptedBytes = getEncryptedMessage(packet);
        int messageLength = messageEncryptedBytes.length;

        if(messageLength > Message.MAX_MESSAGE_LENGTH){
            throw new TooLongMessageException();
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(
                Packet.LENGTH_ALL_WITHOUT_MESSAGE + messageLength);

        putMetadata(byteBuffer, packet, messageEncryptedBytes);
        putMessageBlock(byteBuffer, packet, messageEncryptedBytes);

        return byteBuffer.array();
    }

    private byte[] getEncryptedMessage(Packet packet) throws EncryptionException {
        byte[] messageBytes = packet
                .getUsefulMessage()
                .getMessage()
                .getBytes(StandardCharsets.UTF_8);
        try {
            return cipherEncrypt.doFinal(messageBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException();
        }
    }

    private void putMetadata(ByteBuffer byteBuffer, Packet packet, byte[] messageEncryptedBytes) {
        byteBuffer
                .put(Packet.MAGIC_NUMBER)
                .put(packet.getSource())
                .putLong(packet.getPacketID().longValue())
                .putInt(messageEncryptedBytes.length);

        byte[] metadata = new byte[Packet.LENGTH_METADATA];
        byteBuffer.position(0).get(metadata);
        short metadataCRC = (short) instanceCRC.calculateCRC(metadata);
        byteBuffer.putShort(metadataCRC);
    }

    private void putMessageBlock(ByteBuffer byteBuffer, Packet packet, byte[] messageEncryptedBytes) {
        byteBuffer
                .putInt(packet.getUsefulMessage().getCommandType())
                .putInt(packet.getUsefulMessage().getUserID())
                .put(messageEncryptedBytes);

        byte[] messageBlock = new byte[messageEncryptedBytes.length + Packet.LENGTH_MESSAGE_BLOCK_WITHOUT_MESSAGE];
        byteBuffer.position(Packet.POSITION_MESSAGE_BLOCK).get(messageBlock);
        short messageBlockCRC = (short) instanceCRC.calculateCRC(messageBlock);
        byteBuffer.putShort(messageBlockCRC);
    }

    private class PacketBytesValidator {
        private ByteBuffer buffer;

        PacketBytesValidator() {
        }

        void validate(byte[] packetArray)
                throws CorruptedPacketException {
            buffer = ByteBuffer.wrap(packetArray);
            try {
                validateMagicByte();
                validateMetadata();
                validateMessage();
            } catch (BufferUnderflowException e) {
                throw new HalfPacketException();
            }
        }

        private void validateMagicByte() throws InvalidMagicByteException {
            if (buffer.get(0) != Packet.MAGIC_NUMBER) {
                throw new InvalidMagicByteException();
            }
        }

        private void validateMetadata() throws InvalidCRC16_1_Exception {
            byte[] metadata = new byte[Packet.LENGTH_METADATA];
            buffer.get(metadata);
            short calculatedCRC = (short) instanceCRC.calculateCRC(metadata);
            short packetCRC = buffer.getShort();
            if (packetCRC != calculatedCRC) {
                throw new InvalidCRC16_1_Exception();
            }
        }

        private void validateMessage() throws InvalidCRC16_2_Exception {
            int messageLength = buffer.getInt(10);
            byte[] messageBlock = new byte[Packet.LENGTH_MESSAGE_BLOCK_WITHOUT_MESSAGE + messageLength];
            buffer.position(Packet.POSITION_MESSAGE_BLOCK);
            buffer.get(messageBlock);
            short calculatedCRC = (short) instanceCRC.calculateCRC(messageBlock);
            short packetCRC = buffer.getShort();
            if (packetCRC != calculatedCRC) {
                throw new InvalidCRC16_2_Exception();
            }
        }
    }
}
