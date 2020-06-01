package org.fidoshenyata.network.utils;

import lombok.*;
import org.fidoshenyata.packet.Packet;

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
