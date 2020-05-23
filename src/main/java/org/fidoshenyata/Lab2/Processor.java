package org.fidoshenyata.Lab2;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

public class Processor {

    public Processor(){}

    public Packet process(Packet packet) {
        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 5)
                .packetID(UnsignedLong.valueOf(2))
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(888)
                                .message("Ok")
                                .build()
                );
        return packetBuilder.build();
    }
}
