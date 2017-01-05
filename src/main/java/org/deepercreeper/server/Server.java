package org.deepercreeper.server;

import org.deepercreeper.messages.MessageTypeManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Server<C extends RemoteClient<C>>
{
    private final List<C> clients = new Vector<>();

    private final MessageTypeManager typeManager;

    private final RemoteClientFactory<C> remoteClientFactory;

    private final int port;

    private ServerSocket socket = null;

    private boolean running = false;

    public Server(MessageTypeManager typeManager, RemoteClientFactory<C> remoteClientFactory, int port)
    {
        this.typeManager = typeManager;
        this.remoteClientFactory = remoteClientFactory;
        this.port = port;
    }

    public final boolean isRunning()
    {
        return running;
    }

    public final boolean isConnected()
    {
        return socket != null && !socket.isClosed();
    }

    public final List<C> getClients()
    {
        return new ArrayList<>(clients);
    }

    public final void start()
    {
        if (isRunning() || isConnected())
        {
            throw new IllegalStateException("Cannot start server when still running or connected");
        }
        running = true;
        connect();
    }

    public final void stop()
    {
        if (!isRunning())
        {
            throw new IllegalStateException("Cannot stop not running server");
        }
        running = false;
        for (C client : new ArrayList<>(clients))
        {
            client.stop();
        }
        disconnect();
    }

    public int getPort()
    {
        return port;
    }

    final ServerSocket getSocket()
    {
        return socket;
    }

    final void add(C client)
    {
        clients.add(client);
    }

    final void remove(C client)
    {
        clients.remove(client);
    }

    final MessageTypeManager getTypeManager()
    {
        return typeManager;
    }

    private void connect()
    {
        try
        {
            socket = new ServerSocket(port);
            socket.setReuseAddress(true);
            socket.setSoTimeout(10 * 1000);
            new Thread(new ClientListener<>(this, remoteClientFactory)).start();
        }
        catch (IOException e)
        {
            running = false;
            disconnect();
            throw new RuntimeException(e);
        }
    }

    private void disconnect()
    {
        if (socket == null)
        {
            return;
        }
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        socket = null;
    }
}
