package org.fidoshenyata.server;

import org.fidoshenyata.processor.ProcessorEnum;
import org.fidoshenyata.server.connection.ServerConnectionTCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTCP {

    public static final int THREADS = 5;
    public static final int PROCESSOR_THREADS = 2;
    public static final int PORT = 59898;

    private static ProcessorEnum processorType = ProcessorEnum.OK;

    public static void main(String[] args)  {
        if (args.length == 1) {
            setProcessorType(args[0]);
        }


        try (ServerSocket listener = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            ExecutorService poolConnections = Executors.newFixedThreadPool(THREADS);
            ExecutorService poolProcessors = Executors.newFixedThreadPool(PROCESSOR_THREADS);
            while (true) {
                poolConnections.execute(new ServerConnectionTCP(listener.accept(), poolProcessors,  processorType));
            }
        } catch(IOException e) {
           e.printStackTrace();
        }
    }

    private static void setProcessorType(String choice) {
        switch (choice.toUpperCase()) {
            default:
                processorType = ProcessorEnum.OK;
        }
    }


}
