package org.fidoshenyata;

import org.fidoshenyata.client.ClientCS;
import org.fidoshenyata.client.ClientTCP;
import org.fidoshenyata.server.ServerTCP;
import org.fidoshenyata.exceptions.communication.ServerUnavailableException;
import org.junit.Test;

public class CSTcpTestServerDown {

    @Test(expected = ServerUnavailableException.class)
    public void tryingToConnect() throws Exception {
        ClientCS client = new ClientTCP();
        client.connect();
    }

}
