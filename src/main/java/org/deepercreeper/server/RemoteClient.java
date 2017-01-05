package org.deepercreeper.server;

import org.deepercreeper.messages.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class RemoteClient<C extends RemoteClient<C>>
{
    private final Server<C> server;

    private final Socket socket;

    private final PrintWriter out;

    private final BufferedReader in;

    private Runnable closeAction;

    private boolean running = true;

    public RemoteClient(Server<C> server, Socket socket) throws IOException
    {
        this.server = server;
        this.socket = socket;
        socket.setReuseAddress(true);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public boolean isRunning()
    {
        return running;
    }

    public boolean isConnected()
    {
        return !socket.isClosed() && socket.isConnected();
    }

    public String getAddress()
    {
        if (isConnected())
        {
            return socket.getInetAddress().toString();
        }
        return "<Unknown address>";
    }

    public Server<C> getServer()
    {
        return server;
    }

    public void send(Message message)
    {
        if (!isRunning() || !isConnected())
        {
            throw new IllegalStateException("Cannot send messages when not running or connected");
        }
        out.write(message.encode() + '\n');
        out.flush();
    }

    public void stop()
    {
        if (!isRunning())
        {
            throw new IllegalStateException("Cannot stop server when not running");
        }
        close();
    }

    final void close()
    {
        running = false;
        disconnect();
        disconnected();
        closeAction.run();
    }

    final void init(Runnable closeAction)
    {
        this.closeAction = closeAction;
        new Thread(new MessageListener(this, server.getTypeManager())).start();
    }

    final BufferedReader getIn()
    {
        return in;
    }

    private void disconnect()
    {
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected abstract void receive(Message message);

    protected abstract void disconnected();
}
