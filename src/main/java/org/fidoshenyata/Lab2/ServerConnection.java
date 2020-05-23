package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.model.Packet;

import java.net.Socket;
import java.nio.channels.ClosedChannelException;

public class ServerConnection implements Runnable {
    private Socket socket;

    ServerConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Server opened connection: " + socket);
        try {

            NetworkUtils networkUtils = new NetworkUtils(socket);
            Processor processor = new Processor();

            while (true) {
                try {
                    Packet packet = networkUtils.receiveMessage();
                    System.out.println("Server received: " + packet.getUsefulMessage());

                    Packet answer = processor.process(packet);
                    networkUtils.sendMessage(answer);
                    System.out.println("Server sent");
                } catch (ClosedChannelException e) {
                    break;
                }
            }

            networkUtils.closeStreams();
            socket.close();
            System.out.println("Server closed connection: " + socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}