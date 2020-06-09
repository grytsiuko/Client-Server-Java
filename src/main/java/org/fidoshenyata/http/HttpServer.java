package org.fidoshenyata.http;

import java.io.PrintWriter;
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

            hp.parseRequest();
            System.out.println(hp.getRequestURL());
            System.out.println(hp.getBody());
            System.out.println(hp.getHeaders());
            System.out.println(hp.getParams());
            System.out.println(hp.getMethod());


            PrintWriter out = new PrintWriter(output);

            // Start sending our reply, using the HTTP 1.1 protocol
            out.print("HTTP/1.1 200 \r\n"); // Version & status code
            out.print("Content-Type: application/json\r\n"); // The type of data
            out.print("Connection: close\r\n"); // Will close stream
            out.print("\r\n"); // End of headers
//            out.print("{\"content\":{\"response\":\"ok\"}}");
            out.println(hp.getBody());
            out.close();

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

