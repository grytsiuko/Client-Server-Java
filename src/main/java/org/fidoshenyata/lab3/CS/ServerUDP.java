package org.fidoshenyata.lab3.CS;

import org.fidoshenyata.Lab2.Processor.ProcessorEnum;
import org.fidoshenyata.exceptions.packet.CorruptedPacketException;
import org.fidoshenyata.exceptions.cryption.DecryptionException;
import org.fidoshenyata.exceptions.cryption.KeyInitializationException;
import org.fidoshenyata.lab3.network.NetworkUDP;
import org.fidoshenyata.lab3.network.PacketDestinationInfo;
import org.fidoshenyata.lab3.network.ServerConnectionUDP;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerUDP {
    public static final int THREADS = 5;
    public static final int PORT = 4445;

    private static ProcessorEnum processorType = ProcessorEnum.OK;

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            setProcessorType(args[0]);
        }

        DatagramSocket socket = new DatagramSocket(PORT);
        boolean running = true;

        try {
            NetworkUDP network = new NetworkUDP(socket);
            System.out.println("Server started on Port " + PORT);
            ExecutorService poolProcessor = Executors.newFixedThreadPool(THREADS);
            while (running) {
                try {
                    PacketDestinationInfo packetDI = network.receiveMessage();
                    poolProcessor.execute(new ServerConnectionUDP(packetDI, network, processorType));
                } catch (DecryptionException e) {
                    System.out.println("Decryption error");
                } catch (CorruptedPacketException e) {
                    System.out.println("Corrupted packet");
                }
            }
            network.close();
        } catch (KeyInitializationException e) {
            System.out.println("Unable to start, wrong key");
        }
    }

    private static void setProcessorType(String choice) {
        switch (choice.toUpperCase()) {
            default:
                processorType = ProcessorEnum.OK;
        }
    }
}
