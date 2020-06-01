package org.fidoshenyata.processor;

import org.fidoshenyata.packet.Packet;

public interface Processor {
    Packet process(Packet packet);
}
