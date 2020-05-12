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
    private byte[] crc;


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
        calculateCRC();

        return this;
    }

    private void calculateCRC() {
        long codeCRC = CRC_INSTANCE.calculateCRC(this.message);
        this.crc = new byte[2];

        this.crc[0] = (byte) ((codeCRC >> 8) & 0xff);
        this.crc[1] = (byte) (codeCRC & 0xff);
    }

    public byte[] encode() {
        int messageLength = this.message.length + 8;
        ByteBuffer byteBuffer = ByteBuffer.allocate(18 + messageLength);

        byteBuffer.put(MAGIC_NUMBER);
        byteBuffer.put(this.source);
        byteBuffer.putLong(packetID);
        byteBuffer.putInt(messageLength);

        byteBuffer.put(this.crc);

        byteBuffer.putInt(commandType);
        byteBuffer.putInt(userID);
        byteBuffer.put(this.message);

        byteBuffer.put(this.crc);

        return byteBuffer.array();
    }
}
