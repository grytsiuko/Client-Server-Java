package org.fidoshenyata.lab3;

import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.Lab2.Processor.Processor;
import org.fidoshenyata.Lab2.Processor.ProcessorOkImpl;

import java.io.IOException;

public class ServerConnectionUDP implements Runnable{
    private final PacketDestinationInfo packetDI;
    private final NetworkUDP network;

    public  ServerConnectionUDP(PacketDestinationInfo packetDI, NetworkUDP network){
        this.packetDI = packetDI;
        this.network = network;
    }

    @Override
    public void run() {
        System.out.println("Server processing packet: " + packetDI);
        Processor processor = new ProcessorOkImpl();
        Packet res = processor.process(packetDI.getPacket());
        try {
            network.sendMessage(new PacketDestinationInfo(res,packetDI.getAddress(),packetDI.getPort()));
        } catch (IOException e) {
            System.out.println("Connection was already closed");
        }
    }
}
