package org.fidoshenyata.Lab2.Processor;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

public class ProcessorOkImpl implements Processor {

    public ProcessorOkImpl(){}

    @Override
    public Packet process(Packet packet) {
        if(packet == null) return nullablePacket();
        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 5)
                .packetID(packet.getPacketID())
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(Message.CommandTypes.ADD_PRODUCT.ordinal())
                                .message("Ok")
                                .build()
                );
        return packetBuilder.build();
    }

    public Packet nullablePacket() {
        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 5)
                .packetID(UnsignedLong.valueOf(88))
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(Message.CommandTypes.ADD_PRODUCT.ordinal())
                                .message("Corrupt message was given")
                                .build()
                );
        return packetBuilder.build();
    }
}
