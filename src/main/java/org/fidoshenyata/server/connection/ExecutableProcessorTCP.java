package org.fidoshenyata.server.connection;

import org.fidoshenyata.exceptions.cryption.EncryptionException;
import org.fidoshenyata.exceptions.cryption.TooLongMessageException;
import org.fidoshenyata.network.NetworkTCP;
import org.fidoshenyata.packet.Packet;
import org.fidoshenyata.processor.ProcessorEnum;
import org.fidoshenyata.processor.ProcessorFactory;

import java.io.IOException;

class ExecutableProcessorTCP implements Runnable {

    private final NetworkTCP networkTCP;
    private final Packet packet;
    private final ProcessorEnum processorType;

    ExecutableProcessorTCP(NetworkTCP networkTCP, Packet packet, ProcessorEnum processorType){
        this.networkTCP = networkTCP;
        this.packet = packet;
        this.processorType = processorType;
    }

    @Override
    public void run() {
        try {
            Packet answer = ProcessorFactory.processor(processorType).process(packet);
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
