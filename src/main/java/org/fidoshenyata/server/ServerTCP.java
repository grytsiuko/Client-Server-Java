package org.fidoshenyata.server;

import org.fidoshenyata.server.connection.ServerConnectionTCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTCP implements ServerCS {

    public static final int THREADS = 5;
    public static final int PROCESSOR_THREADS = 2;
    public static final int PORT = 59898;

    public ServerTCP(){
    }

    public static void main(String[] args)  {
        new Thread(new ServerTCP()).start();
    }


    @Override
    public void run() {
        try (ServerSocket listener = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            ExecutorService poolConnections = Executors.newFixedThreadPool(THREADS);
            ExecutorService poolProcessors = Executors.newFixedThreadPool(PROCESSOR_THREADS);
            while (true) {
                poolConnections.execute(new ServerConnectionTCP(listener.accept(), poolProcessors));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
