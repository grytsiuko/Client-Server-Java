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
    private static final int NTHREADS = 100;
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
            System.out.println(hp.getRequestURL());
            System.out.println(hp.getHeader("x-auth"));
            System.out.println(hp.getParams());
            System.out.println(hp.getMethod());

            ResponseSender responseSender = new ResponseSender(output);
            HttpProcessor httpProcessor = new HttpProcessor(new ProductionConnectionFactory(),hp, responseSender);
            httpProcessor.process();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void await() {
        int port = 8891;

        // Loop waiting for a request
        try (
                ServerSocket socket = new ServerSocket(port);
        ) {
            System.out.println("Server is waiting for request at port: " + port);
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

