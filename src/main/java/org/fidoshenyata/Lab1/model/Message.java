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
    private final int commandType;
    private final int userID;
    private final String message;
}
