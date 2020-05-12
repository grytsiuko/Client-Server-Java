package org.fidoshenyata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Packet {

    private final byte source;
    private final long packetID;
    private final int commandType;
    private final int userID;
    private final String message;

}
