package org.fidoshenyata.server;

import org.fidoshenyata.processor.ProcessorEnum;
import org.fidoshenyata.processor.ProcessorFactory;
import org.fidoshenyata.server.connection.ServerConnectionTCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTCP implements ServerCS {

    public static final int THREADS = 5;
    public static final int PROCESSOR_THREADS = 2;
    public static final int PORT = 59898;

    private ProcessorEnum processorType;

    public ServerTCP(ProcessorEnum processorType){
        this.processorType = processorType;
    }

    public static void main(String[] args)  {
        ProcessorEnum processorEnum = args.length == 1 ? ProcessorFactory.processorType(args[0]): ProcessorEnum.OK;
        new Thread(new ServerTCP(processorEnum)).start();
    }


    @Override
    public void run() {
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
}
