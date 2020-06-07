package org.fidoshenyata.packet;

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


    public static final int PING = 0;

    public static final int RESPONSE_OK = 1;
    public static final int RESPONSE_ERROR = 2;


    public static final int COMMAND_GET_CATEGORIES = 3;
    public static final int COMMAND_GET_CATEGORIES_BY_NAME = 4;
    public static final int COMMAND_GET_CATEGORY_BY_ID = 5;
    public static final int COMMAND_ADD_CATEGORY = 6;
    public static final int COMMAND_UPDATE_CATEGORY = 7;
    public static final int COMMAND_DELETE_CATEGORY = 8;
    public static final int COMMAND_DELETE_ALL_CATEGORIES = 9;


    public static final int COMMAND_GET_PRODUCTS = 10;
    public static final int COMMAND_GET_PRODUCTS_BY_CATEGORY = 11;
    public static final int COMMAND_GET_PRODUCTS_BY_NAME = 12;
    public static final int COMMAND_GET_PRODUCTS_BY_NAME_BY_CATEGORY = 13;
    public static final int COMMAND_GET_PRODUCT_BY_ID = 14;
    public static final int COMMAND_GET_PRODUCTS_COST = 15;
    public static final int COMMAND_GET_PRODUCTS_COST_BY_CATEGORY = 16;
    public static final int COMMAND_ADD_PRODUCT = 17;
    public static final int COMMAND_UPDATE_PRODUCT = 18;
    public static final int COMMAND_INCREASE_PRODUCT = 19;
    public static final int COMMAND_DECREASE_PRODUCT = 20;
    public static final int COMMAND_DELETE_PRODUCT = 21;
    public static final int COMMAND_DELETE_ALL_PRODUCTS = 22;
}
