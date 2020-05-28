package org.fidoshenyata.lab3.network;

import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.Lab2.Processor.Processor;
import org.fidoshenyata.Lab2.Processor.ProcessorEnum;
import org.fidoshenyata.Lab2.Processor.ProcessorFactory;
import org.fidoshenyata.exceptions.cryption.EncryptionException;

import java.io.IOException;

public class ServerConnectionUDP implements Runnable{
    private final PacketDestinationInfo packetDI;
    private final NetworkUDP network;
    private final ProcessorEnum processorType;

    public  ServerConnectionUDP(PacketDestinationInfo packetDI, NetworkUDP network, ProcessorEnum processorType){
        this.packetDI = packetDI;
        this.network = network;
        this.processorType = processorType;
    }

    @Override
    public void run() {
        System.out.println("Server processing packet: " + packetDI);
        Processor processor = ProcessorFactory.processor(processorType);
        Packet res = processor.process(packetDI.getPacket());
        try {
            network.sendMessage(new PacketDestinationInfo(res,packetDI.getAddress(),packetDI.getPort()));
        } catch (IOException e) {
            System.out.println("Connection was already closed");
        } catch (EncryptionException e) {
            System.out.println("Error while encryption");
        }
    }
}
