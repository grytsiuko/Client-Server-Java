package org.fidoshenyata.processor;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.packet.Message;
import org.fidoshenyata.packet.Message.*;
import org.fidoshenyata.packet.Packet;

public class ProcessorCorrectImpl implements Processor {

    public ProcessorCorrectImpl() {
    }

    @Override
    public Packet process(Packet packet) {
        Message inputMessage = packet.getUsefulMessage();
        Message answerMessage = processMessage(inputMessage);

        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source(packet.getSource())
                .packetID(packet.getPacketID())
                .usefulMessage(answerMessage);
        return packetBuilder.build();
    }

    private Message processMessage(Message message) {
//        switch (message.getCommandType()) {
//            case COMMAND_GET_CATEGORIES:
//
//                break;
//            default:
//                return Message.builder().userID(message.getUserID())
//                        .commandType(ME)
//        }
        return null;
    }

    public static void main(String[] args) {

    }
}
