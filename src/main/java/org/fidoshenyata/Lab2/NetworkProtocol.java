package org.fidoshenyata.Lab2;

import org.fidoshenyata.Lab1.model.Packet;

import java.io.InputStream;
import java.io.OutputStream;

public interface NetworkProtocol {
    Packet receiveMessage(InputStream inputStream) throws Exception;

    void sendMessage(Packet packet, OutputStream outputStream) throws Exception;
}
