package org.fidoshenyata.server;

import org.fidoshenyata.exceptions.packet.CorruptedPacketException;
import org.fidoshenyata.exceptions.cryption.DecryptionException;
import org.fidoshenyata.exceptions.cryption.KeyInitializationException;
import org.fidoshenyata.network.NetworkUDP;
import org.fidoshenyata.network.utils.PacketDestinationInfo;
import org.fidoshenyata.server.connection.ServerConnectionUDP;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerUDP implements ServerCS{
    public static final int THREADS = 5;
    public static final int PORT = 4445;

    public ServerUDP(){
    }

    public static void main(String[] args){
        new Thread(new ServerTCP()).start();
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(PORT);
            boolean running = true;
            NetworkUDP network = new NetworkUDP(socket);
            System.out.println("Server started on Port " + PORT);
            ExecutorService poolProcessor = Executors.newFixedThreadPool(THREADS);
            while (running) {
                try {
                    PacketDestinationInfo packetDI = network.receiveMessage();
                    poolProcessor.execute(new ServerConnectionUDP(packetDI, network));
                } catch (DecryptionException e) {
                    System.out.println("Decryption error");
                } catch (CorruptedPacketException e) {
                    System.out.println("Corrupted packet");
                }
            }
            network.close();
        } catch (KeyInitializationException  e) {
            System.out.println("Unable to start, wrong key");
        } catch (IOException e ){
            System.out.println("Something went wrong");
        }
    }
}
