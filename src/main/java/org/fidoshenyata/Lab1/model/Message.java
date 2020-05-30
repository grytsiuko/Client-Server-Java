package org.fidoshenyata.Lab1.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Message {
    private final Integer commandType;
    private final Integer userID;
    private final String message;

    public final static Integer MAX_MESSAGE_LENGTH = 512;

    public enum CommandTypes {
        GET_PRODUCT_COUNT,
        GET_PRODUCT,
        ADD_PRODUCT,
        ADD_PRODUCT_TITLE,
        SET_PRODUCT_PRICE,
        ADD_PRODUCT_TO_GROUP
    }
}
