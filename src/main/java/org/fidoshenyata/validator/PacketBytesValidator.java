package org.fidoshenyata.validator;

import com.github.snksoft.crc.CRC;

import java.nio.ByteBuffer;

public class PacketBytesValidator implements Validator<byte[]> {

    private static final CRC crcInstance = new CRC(CRC.Parameters.CRC16);

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
        short calculatedCRC = (short) crcInstance.calculateCRC(metadata);
        short packetCRC = buffer.getShort();
        return packetCRC == calculatedCRC;
    }

    private boolean isUncorruptedMessage() {
        int messageLength = buffer.getInt(10);
        byte[] message = new byte[messageLength];
        buffer.position(16);
        buffer.get(message);
        short calculatedCRC = (short) crcInstance.calculateCRC(message);
        short packetCRC = buffer.getShort();
        return packetCRC == calculatedCRC;
    }
}
