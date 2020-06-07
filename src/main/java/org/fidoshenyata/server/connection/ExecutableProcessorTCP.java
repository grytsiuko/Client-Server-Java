package org.fidoshenyata.server.connection;

import org.fidoshenyata.exceptions.cryption.EncryptionException;
import org.fidoshenyata.exceptions.cryption.TooLongMessageException;
import org.fidoshenyata.network.NetworkTCP;
import org.fidoshenyata.packet.Packet;
import org.fidoshenyata.processor.Processor;

import java.io.IOException;

class ExecutableProcessorTCP implements Runnable {

    private final NetworkTCP networkTCP;
    private final Packet packet;

    ExecutableProcessorTCP(NetworkTCP networkTCP, Packet packet){
        this.networkTCP = networkTCP;
        this.packet = packet;
    }

    @Override
    public void run() {
        try {
            Packet answer = new Processor().process(packet);
            networkTCP.sendMessage(answer);
            System.out.println("Server sent response");
        } catch (EncryptionException e) {
            System.out.println("Error while encrypting");
        } catch (IOException e) {
            System.out.println("IO Error occurred, client might have closed connection");
        } catch (TooLongMessageException e) {
            System.out.println("Too long message");
        }
    }
}
