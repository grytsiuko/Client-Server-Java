package org.fidoshenyata.processor;

import org.fidoshenyata.packet.Message;

import static org.fidoshenyata.packet.Message.RESPONSE_ERROR;
import static org.fidoshenyata.packet.Message.RESPONSE_OK;

public class ProcessorUtils {


    public Message buildErrorMessage(String message, int userID) {
        return Message.builder()
                .userID(userID)
                .commandType(RESPONSE_ERROR)
                .message(message)
                .build();
    }


    public Message buildSuccessMessage(String message, int userID) {
        return Message.builder()
                .userID(userID)
                .commandType(RESPONSE_OK)
                .message(message)
                .build();
    }
}
