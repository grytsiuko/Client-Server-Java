package org.fidoshenyata.Lab1.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class Packet {
    private final byte source;
    private final long packetID;
    private final Message usefulMessage;

}
