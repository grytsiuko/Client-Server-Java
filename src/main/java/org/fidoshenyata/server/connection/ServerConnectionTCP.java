package org.fidoshenyata.server.connection;

import org.fidoshenyata.packet.Packet;
import org.fidoshenyata.network.NetworkTCP;
import org.fidoshenyata.exceptions.communication.SocketClosedException;
import org.fidoshenyata.exceptions.cryption.DecryptionException;
import org.fidoshenyata.exceptions.cryption.FailedHandShake;
import org.fidoshenyata.exceptions.packet.CorruptedPacketException;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ServerConnectionTCP implements Runnable {

    private final Socket socket;
    private final ExecutorService poolProcessors;

    public ServerConnectionTCP(Socket socket, ExecutorService poolProcessors) {
        this.socket = socket;
        this.poolProcessors = poolProcessors;
    }

    @Override
    public void run() {
        System.out.println("Server opened connection: " + socket);

        try {

            startRunning();

        } catch (FailedHandShake e) {
            System.out.println("HandShake failed");
        } catch (IOException e) {
            System.out.println("IO Error occurred");
        }

        closeConnection();
    }

    private void startRunning() throws IOException, FailedHandShake {
        NetworkTCP networkTCP = new NetworkTCP(socket);

        while (true) {
            try {
                Packet packet = networkTCP.receiveMessage();
                System.out.println("Server received: " + packet.getUsefulMessage());
                poolProcessors.execute(new ExecutableProcessorTCP(networkTCP, packet));

            } catch (SocketClosedException e) {
                System.out.println("Socket was closed by client, unable to receive packet");
                break;
            } catch (DecryptionException e) {
                System.out.println("Error while decrypting");
            } catch (CorruptedPacketException e) {
                System.out.println("Packet corrupted");
            }
        }

        networkTCP.closeStreams();
    }

    private void closeConnection(){
        try {
            System.out.println("Server is closing socket: " + socket);
            socket.close();
        } catch (IOException e) {
            System.out.println("IO Error occurred while closing socket");
        }
    }

}