package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;

public class Processor {

    public Processor(){}

    public Packet process(Packet packet) {
        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source((byte) 5)
                .packetID((long) 2)
                .usefulMessage(
                        Message.builder()
                                .userID(2048)
                                .commandType(888)
                                .message("Hello From Server!")
                                .build()
                );
        return packetBuilder.build();
    }
}
