package org.fidoshenyata.client;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.exceptions.communication.NoAnswerException;
import org.fidoshenyata.exceptions.communication.RequestInterruptedException;
import org.fidoshenyata.exceptions.communication.ServerUnavailableException;
import org.fidoshenyata.exceptions.cryption.*;
import org.fidoshenyata.exceptions.packet.CorruptedPacketException;
import org.fidoshenyata.packet.Message;
import org.fidoshenyata.packet.Packet;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public interface ClientCS {
    UnsignedLong getPacketCount();
    void connect() throws KeyInitializationException, SocketException, UnknownHostException,
            FailedHandShake, ServerUnavailableException;

    Packet request(Message message)
            throws EncryptionException, DecryptionException, CorruptedPacketException,
            NoAnswerException, TooLongMessageException, ServerUnavailableException,
            FailedHandShake, RequestInterruptedException;

    void requestGivingHalfTEST(Message message)
                    throws IOException, EncryptionException, TooLongMessageException;

    void disconnect() throws IOException;
}
