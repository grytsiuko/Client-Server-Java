package org.fidoshenyata.lab3;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerUDP {
    public static final int THREADS = 5;
    public static final int PORT = 4445;

    public static void main(String[] args) throws IOException {
       DatagramSocket socket = new DatagramSocket(PORT);
       boolean running = true;
       NetworkUDP network = new NetworkUDP(socket);
        System.out.println("Server started on Port "+ PORT);
       ExecutorService poolProcessor = Executors.newFixedThreadPool(THREADS);
       while(running){
           PacketDestinationInfo packetDI = network.receiveMessage();
           poolProcessor.execute(new ServerConnectionUDP(packetDI, network));
       }
       network.close();
    }
}
