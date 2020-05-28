package org.fidoshenyata.Lab2.Network;

import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.Lab2.Processor.Processor;
import org.fidoshenyata.Lab2.Processor.ProcessorOkImpl;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;

public class ServerConnectionTCP implements Runnable {
    private final Socket socket;

    public ServerConnectionTCP(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Server opened connection: " + socket);
        try {

            NetworkTCP networkTCP = new NetworkTCP(socket);
            Processor processor = new ProcessorOkImpl();

            while (true) {
                try {
                    Packet packet = networkTCP.receiveMessage();
                    System.out.println("Server received: " + packet.getUsefulMessage());

                    Packet answer = processor.process(packet);
                    networkTCP.sendMessage(answer);
                    System.out.println("Server sent");
                } catch (ClosedChannelException e) {
                    System.out.println("Socket was closed");
                    break;
                }
            }

            networkTCP.closeStreams();
            socket.close();
            System.out.println("Server closed connection: " + socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}