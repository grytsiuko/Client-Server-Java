package org.fidoshenyata.Lab2.Processor;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

public class ProcessorOkImpl implements Processor {

    public ProcessorOkImpl(){}

    @Override
    public Packet process(Packet packet) {
        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 5)
                .packetID(UnsignedLong.valueOf(2))
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(Message.CommandTypes.ADD_PRODUCT.ordinal())
                                .message("Ok")
                                .build()
                );
        return packetBuilder.build();
    }
}
