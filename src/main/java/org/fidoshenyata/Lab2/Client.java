package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.model.Packet;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    private NetworkProtocol networkProtocol;
    private Socket socket;

    public Packet send(int serverPort, Packet packet) throws Exception{
        try {

            socket = new Socket("localhost", serverPort);
            System.out.println("Client Connected");

            var in = socket.getInputStream();
            var out = socket.getOutputStream();

            networkProtocol.sendMessage(packet, out);
            System.out.println("Client sent");
//            Packet reply = networkProtocol.receiveMessage(in);
//            System.out.println("Client received");
//            return reply;
            in.close();
            out.close();
            return null;
        } finally {
            System.out.println("Client closed");
            socket.close();
        }

    }

    public Client() throws Exception {
        this.networkProtocol = new NetworkProtocolTCP();
    }

}
