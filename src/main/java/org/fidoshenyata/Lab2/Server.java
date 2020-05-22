package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.model.Packet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int THREADS = 20;
    public static final int PORT = 59898;

    public static void main(String[] args) {
        try (ServerSocket listener = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            ExecutorService pool = Executors.newFixedThreadPool(THREADS);
            while (true) {
                pool.execute(new Runner(listener.accept()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Runner implements Runnable {
        private Socket socket;

        Runner(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Server Connected: " + socket);
            try {

                NetworkUtils networkUtils = new NetworkUtils(socket);
                Processor processor = new Processor();

//                Packet packet = networkUtils.receiveMessage();
//                System.out.println("Server received: " + packet.getUsefulMessage());
//
//                Packet answer = processor.process(packet);
//                networkUtils.sendMessage(answer);
//                System.out.println("Server sent");

                networkUtils.closeStreams();
            } catch (Exception e) {
                System.out.println("Server Error:" + socket);
                e.printStackTrace();
            } finally {
                try {

                    socket.close();
                } catch (IOException e) {
                }
                System.out.println("Server Closed: " + socket);
            }
        }

    }

}
