package org.fidoshenyata.processor;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.packet.Message;
import org.fidoshenyata.packet.Packet;

public class ProcessorOkImpl implements Processor {

    public ProcessorOkImpl(){}

    @Override
    public Packet process(Packet packet) {
        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 5)
                .packetID(packet.getPacketID())
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(Message.COMMAND_ADD_CATEGORY)
                                .message("Ok")
                                .build()
                );
        return packetBuilder.build();
    }
}
