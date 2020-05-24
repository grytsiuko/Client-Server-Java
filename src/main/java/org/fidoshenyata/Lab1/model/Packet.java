package org.fidoshenyata.Lab1.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import com.google.common.primitives.UnsignedLong;

@Getter
@Builder
@EqualsAndHashCode
public class Packet {

    public final static Byte MAGIC_NUMBER = 0x13;

    private final Byte source;
    private final UnsignedLong packetID;
    private final Message usefulMessage;

    public final static Integer POSITION_MAGIC_BYTE = 0;
    public final static Integer POSITION_SOURCE = POSITION_MAGIC_BYTE + Byte.BYTES;
    public final static Integer POSITION_PACKET_ID = POSITION_SOURCE + Byte.BYTES;
    public final static Integer POSITION_LENGTH = POSITION_PACKET_ID + Long.BYTES;
    public final static Integer POSITION_CRC_1 = POSITION_LENGTH + Integer.BYTES;
    public final static Integer POSITION_MESSAGE_BLOCK = POSITION_CRC_1 + Short.BYTES;
    public final static Integer POSITION_COMMAND_TYPE = POSITION_MESSAGE_BLOCK;
    public final static Integer POSITION_USER_ID = POSITION_COMMAND_TYPE + Integer.BYTES;
    public final static Integer POSITION_MESSAGE = POSITION_USER_ID + Integer.BYTES;

    public final static Integer LENGTH_MAGIC_BYTE = Byte.BYTES;
    public final static Integer LENGTH_METADATA = POSITION_CRC_1;
    public final static Integer LENGTH_METADATA_WITHOUT_LENGTH = POSITION_LENGTH;
    public final static Integer LENGTH_MESSAGE_BLOCK_WITHOUT_MESSAGE = Integer.BYTES + Integer.BYTES;
    public final static Integer LENGTH_ALL_WITHOUT_MESSAGE = POSITION_MESSAGE + Short.BYTES;
}
