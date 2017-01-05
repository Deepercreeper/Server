package org.deepercreeper.client;

import org.deepercreeper.messages.Message;
import org.deepercreeper.messages.MessageTypeManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class Client
{
    private final MessageTypeManager typeManager;

    private final String address;

    private final int port;

    private final int localPort;

    private int timeout = 5000;

    private Socket socket = null;

    private boolean running = false;

    private PrintWriter out;

    private BufferedReader in;

    public Client(MessageTypeManager typeManager, String address, int port, int localPort)
    {
        this.typeManager = typeManager;
        this.address = address;
        this.port = port;
        this.localPort = localPort;
    }

    public Client(MessageTypeManager typeManager, String address, int port)
    {
        this(typeManager, address, port, -1);
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public boolean isRunning()
    {
        return running;
    }

    public boolean isConnected()
    {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public String getAddress()
    {
        if (isConnected())
        {
            return socket.getInetAddress().toString();
        }
        return "<Unknown address>";
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

    public void start()
    {
        if (isRunning() || isConnected())
        {
            throw new IllegalStateException("Cannot start client when still running or connected");
        }
        running = true;
        connect();
    }

    public void stop()
    {
        if (!isRunning())
        {
            throw new IllegalStateException("Cannot stop when not running");
        }
        close();
    }

    final void close()
    {
        running = false;
        disconnect();
        disconnected();
    }

    final BufferedReader getIn()
    {
        return in;
    }

    private void connect()
    {
        try
        {
            socket = new Socket();
            socket.setReuseAddress(true);
            if (localPort >= 0)
            {
                socket.bind(new InetSocketAddress(localPort));
            }
            socket.connect(new InetSocketAddress(address, port), timeout);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(new MessageListener(this, typeManager)).start();
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
        if (socket != null)
        {
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
        out = null;
        in = null;
    }

    protected abstract void receive(Message message);

    protected abstract void disconnected();
}
