package org.deepercreeper.server;

import java.io.IOException;
import java.net.Socket;

public interface RemoteClientFactory<C extends RemoteClient<C>>
{
    C create(Server<C> server, Socket socket) throws IOException;
}
