package org.fidoshenyata.server.connection;

import org.fidoshenyata.packet.Packet;
import org.fidoshenyata.processor.Processor;
import org.fidoshenyata.exceptions.cryption.EncryptionException;
import org.fidoshenyata.exceptions.cryption.TooLongMessageException;
import org.fidoshenyata.network.NetworkUDP;
import org.fidoshenyata.network.utils.PacketDestinationInfo;

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
        Processor processor = new Processor();
        Packet res = processor.process(packetDI.getPacket());
        try {
            network.sendMessage(new PacketDestinationInfo(res,packetDI.getAddress(),packetDI.getPort()));
        } catch (IOException e) {
            System.out.println("Connection was already closed");
        } catch (EncryptionException e) {
            System.out.println("Error while encryption");
        } catch (TooLongMessageException e) {
            System.out.println("Too long message");
        }
    }
}
