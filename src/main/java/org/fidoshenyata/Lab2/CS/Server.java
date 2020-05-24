package org.fidoshenyata.Lab2.CS;

import org.fidoshenyata.Lab2.Network.ServerConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final int THREADS = 5;
    public static final int PORT = 59898;

    public static void main(String[] args)  {
        try (ServerSocket listener = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            ExecutorService pool = Executors.newFixedThreadPool(THREADS);
            while (true) {
                pool.execute(new ServerConnection(listener.accept()));
            }
        } catch(IOException e) {
           e.printStackTrace();
        }
    }


}
