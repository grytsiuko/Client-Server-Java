package org.fidoshenyata.Lab2;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int THREADS = 5;
    public static final int PORT = 59898;

    public static void main(String[] args) throws Exception {
        try (ServerSocket listener = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            ExecutorService pool = Executors.newFixedThreadPool(THREADS);
            while (true) {
                pool.execute(new ServerConnection(listener.accept()));
            }
        }
    }


}
