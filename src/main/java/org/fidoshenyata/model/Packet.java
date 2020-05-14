package org.fidoshenyata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Packet {

    private final byte source;
    private final long packetID;
    private final int commandType;
    private final int userID;
    private final String message;

}
