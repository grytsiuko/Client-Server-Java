package org.fidoshenyata.lab3.network;

import lombok.*;
import org.fidoshenyata.Lab1.model.Packet;

import java.net.InetAddress;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PacketDestinationInfo {
    private final Packet packet;
    private final InetAddress address;
    private final int port;
}
