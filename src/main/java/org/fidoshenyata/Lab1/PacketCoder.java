package org.fidoshenyata.Lab1;

import com.github.snksoft.crc.CRC;
import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.exceptions.InvalidCRC16_1_Exception;
import org.fidoshenyata.exceptions.InvalidCRC16_2_Exception;
import org.fidoshenyata.exceptions.InvalidMagicByteException;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;

public class PacketCoder {

    private final CRC instanceCRC;
    private final PacketBytesValidator packetBytesValidator;
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

        instanceCRC = new CRC(CRC.Parameters.CRC16);
        packetBytesValidator = new PacketBytesValidator();
    }

    public byte[] encode(Packet packet) throws Exception {
        if (packet == null) {
            throw new IllegalArgumentException("Packet is not defined");
        }
        return encodeLegalState(packet);
    }

    public Packet decode(byte[] packetArray) throws Exception {
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

    private String getDecodedMessage(ByteBuffer buffer) throws Exception {
        int messageLength = buffer.getInt(Packet.POSITION_LENGTH);
        byte[] message = new byte[messageLength];
        buffer.position(Packet.POSITION_MESSAGE);
        buffer.get(message);
        return new String(cipherDecrypt.doFinal(message));
    }

    private byte[] encodeLegalState(Packet packet) throws Exception {
        byte[] messageEncryptedBytes = getEncryptedMessage(packet);
        ByteBuffer byteBuffer = ByteBuffer.allocate(
                Packet.LENGTH_ALL_WITHOUT_MESSAGE + messageEncryptedBytes.length);

        putMetadata(byteBuffer, packet, messageEncryptedBytes);
        putMessageBlock(byteBuffer, packet, messageEncryptedBytes);

        return byteBuffer.array();
    }

    private byte[] getEncryptedMessage(Packet packet) throws Exception {
        byte[] messageBytes = packet
                .getUsefulMessage().getMessage().getBytes(StandardCharsets.UTF_8);
        return cipherEncrypt.doFinal(messageBytes);
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

        public PacketBytesValidator() {
        }

        public void validate(byte[] packetArray) throws Exception{
            buffer = ByteBuffer.wrap(packetArray);
            validateMagicByte();
            validateMetadata();
            validateMessage();
        }

        private void validateMagicByte() throws Exception{
            if(buffer.get(0) != Packet.MAGIC_NUMBER){
                throw new InvalidMagicByteException();
            }
        }

        private void validateMetadata() throws Exception{
            byte[] metadata = new byte[Packet.LENGTH_METADATA];
            buffer.get(metadata);
            short calculatedCRC = (short) instanceCRC.calculateCRC(metadata);
            short packetCRC = buffer.getShort();
            if(packetCRC != calculatedCRC){
                throw new InvalidCRC16_1_Exception();
            }
        }

        private void validateMessage() throws Exception{
            int messageLength = buffer.getInt(10);
            byte[] messageBlock = new byte[Packet.LENGTH_MESSAGE_BLOCK_WITHOUT_MESSAGE + messageLength];
            buffer.position(Packet.POSITION_MESSAGE_BLOCK);
            buffer.get(messageBlock);
            short calculatedCRC = (short) instanceCRC.calculateCRC(messageBlock);
            short packetCRC = buffer.getShort();
            if(packetCRC != calculatedCRC){
                throw new InvalidCRC16_2_Exception();
            }
        }
    }
}
