package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.model.Packet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
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
                var in = socket.getInputStream();
                var out = socket.getOutputStream();

                Key key = doHandShake(in, out);
                NetworkUtils networkUtils = new NetworkUtils(key);
                Processor processor = new Processor();

                Packet packet = networkUtils.receiveMessage(in);
                System.out.println("Server received: " + packet.getUsefulMessage());

                Packet answer = processor.process(packet);
                networkUtils.sendMessage(answer, out);
                System.out.println("Server sent");

                in.close();
                out.close();
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

        private Key doHandShake(InputStream inputStream, OutputStream outputStream) throws Exception{
            Keys keys = new Keys();

            PublicKey publicKey = keys.getPublicKey();
            byte[] publicKeyEncoded = publicKey.getEncoded();
            outputStream.write(publicKeyEncoded);
            outputStream.write(0x13);
            outputStream.flush();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (int oneChar; (oneChar = inputStream.read()) != 0x13;)
                buffer.write(oneChar);

            PublicKey clientPublicKey =
                    KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(buffer.toByteArray()));
            keys.setReceiverPublicKey(clientPublicKey);

            return keys.generateKey();
        }

    }

}
