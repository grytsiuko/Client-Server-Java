package org.fidoshenyata;

import com.github.snksoft.crc.CRC;

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

    private Byte source;
    private Long packetID;
    private int commandType;
    private int userID;
    private byte[] message;


    public PacketEncoder() {
    }

    public PacketEncoder setSource(byte source) {
        this.source = source;
        return this;
    }

    public PacketEncoder setPacketID(long packetID) {
        this.packetID = packetID;
        return this;
    }

    public PacketEncoder setUserID(int userID) {
        this.userID = userID;
        return this;
    }

    public PacketEncoder setCommandType(int commandType) {
        this.commandType = commandType;
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

    public PacketEncoder setMessage(String message) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, this.key);
        this.message = this.cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        return this;
    }

    public byte[] encode() {
        int messageBlockLength = this.message.length + 8;
        ByteBuffer byteBuffer = ByteBuffer.allocate(18 + messageBlockLength);

        byteBuffer.put(MAGIC_NUMBER);
        byteBuffer.put(this.source);
        byteBuffer.putLong(packetID);
        byteBuffer.putInt(messageBlockLength);

        byte[] metadata = new byte[14];
        byteBuffer.position(0).get(metadata);
        short metadataCRC = (short)CRC_INSTANCE.calculateCRC(metadata);
        byteBuffer.putShort(metadataCRC);

        byteBuffer.putInt(commandType);
        byteBuffer.putInt(userID);
        byteBuffer.put(this.message);

        short messageCRC = (short)CRC_INSTANCE.calculateCRC(this.message);
        byteBuffer.putShort(messageCRC);

        return byteBuffer.array();
    }
}
