package org.fidoshenyata.Lab2.Network;

import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.Lab2.Processor.Processor;
import org.fidoshenyata.Lab2.Processor.ProcessorOkImpl;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;

public class ServerConnection implements Runnable {
    private final Socket socket;

    public ServerConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Server opened connection: " + socket);
        try {

            NetworkUtils networkUtils = new NetworkUtils(socket);
            Processor processor = new ProcessorOkImpl();

            while (true) {
                try {
                    Packet packet = networkUtils.receiveMessage();
                    System.out.println("Server received: " + packet.getUsefulMessage());

                    Packet answer = processor.process(packet);
                    networkUtils.sendMessage(answer);
                    System.out.println("Server sent");
                } catch (ClosedChannelException e) {
                    System.out.println("Socket was closed");
                    break;
                }
            }

            networkUtils.closeStreams();
            socket.close();
            System.out.println("Server closed connection: " + socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}