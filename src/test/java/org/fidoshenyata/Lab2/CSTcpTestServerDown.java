package org.fidoshenyata.Lab2;

import com.google.common.primitives.UnsignedLong;
import org.fidoshenyata.Lab1.model.Message;
import org.fidoshenyata.Lab1.model.Packet;
import org.fidoshenyata.Lab2.CS.ClientTCP;
import org.fidoshenyata.Lab2.CS.ServerTCP;
import org.fidoshenyata.exceptions.communication.ServerUnavailableException;
import org.junit.Before;
import org.junit.Test;

public class CSTcpTestServerDown {

    @Test(expected = ServerUnavailableException.class)
    public void tryingToConnect() throws Exception {
        ClientTCP client = new ClientTCP();
        client.connect(ServerTCP.PORT + 1);
    }

}
