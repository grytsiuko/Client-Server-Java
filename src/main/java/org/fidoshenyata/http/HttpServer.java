package org.fidoshenyata.http;

import org.fidoshenyata.db.connection.ProductionConnectionFactory;

import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.net.ServerSocket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class HttpServer {
    private final static int NTHREADS = 100;

    private final static int PORT = 8891;
    private static final Executor exec
            = Executors.newFixedThreadPool(NTHREADS);

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.await();
    }

    public void handleRequest(Socket connection) {
        try (
                InputStream input = connection.getInputStream();
                OutputStream output = connection.getOutputStream();
        ) {
            HttpParser hp = new HttpParser(input);
            ResponseSender responseSender = new ResponseSender(output);
            HttpProcessor httpProcessor = new HttpProcessor(new ProductionConnectionFactory(),hp, responseSender);
            httpProcessor.process();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void await() {
        // Loop waiting for a request
        try (
                ServerSocket socket = new ServerSocket(PORT);
        ) {
            System.out.println("Server is waiting for request at port: " + PORT);
            while (true) {
                Socket connection = socket.accept();
                Runnable task = () -> handleRequest(connection);
                exec.execute(task);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


}

