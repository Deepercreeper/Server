package org.deepercreeper.server;

import org.deepercreeper.client.Client;
import org.deepercreeper.common.data.Bundle;
import org.deepercreeper.common.util.Util;
import org.deepercreeper.messages.Message;
import org.deepercreeper.messages.MessageTypeManager;
import org.deepercreeper.messages.TestMessageType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ServerTest
{
    private static final Set<Integer> PORTS = new HashSet<>();

    @Test
    @Ignore
    public void testServer()
    {
        MessageTypeManager typeManager = new MessageTypeManager(TestMessageType.CLIENT);
        Server<FrameRemoteClient> server = new Server<>(typeManager, FrameRemoteClient.FACTORY, generatePort());
        server.start();
        boolean stop = false;
        //noinspection ConstantConditions
        while (!stop)
        {
            Util.sleep(1000);
        }
        server.stop();
    }

    @Test
    public void testConnect()
    {
        int port = generatePort();
        Server<TestRemoteClient> server = startServer(port);
        TestClient client = startClient(port, generatePort());
        TestRemoteClient remoteClient = getRemoteClient(server);

        Assert.assertTrue(remoteClient.connected);
        Assert.assertTrue(client.connected);

        server.stop();
    }

    @Test
    public void testServerDisconnect()
    {
        int port = generatePort();
        Server<TestRemoteClient> server = startServer(port);
        TestClient client = startClient(port, generatePort());
        TestRemoteClient remoteClient = getRemoteClient(server);

        server.stop();

        assertDisconnected(server, remoteClient, client);
    }

    @Test
    public void testClientDisconnect()
    {
        int port = generatePort();
        Server<TestRemoteClient> server = startServer(port);
        TestClient client = startClient(port, generatePort());
        TestRemoteClient remoteClient = getRemoteClient(server);

        client.stop();

        assertDisconnected(server, remoteClient, client);

        server.stop();
    }

    @Test
    public void testMessaging()
    {
        int port = generatePort();
        Server<TestRemoteClient> server = startServer(port);
        TestClient client = startClient(port, generatePort());
        TestRemoteClient remoteClient = getRemoteClient(server);

        Message message = new Message(TestMessageType.CLIENT, new Bundle().put("message", "Message"));
        client.send(message);

        Util.sleep(100);

        Assert.assertTrue(remoteClient.receivedMessage);
        Assert.assertTrue(client.receivedMessage);

        server.stop();
    }

    private TestRemoteClient getRemoteClient(Server<TestRemoteClient> server)
    {
        Util.sleep(100);

        return server.getClients().get(0);
    }

    private Server<TestRemoteClient> startServer(int port)
    {
        MessageTypeManager typeManager = new MessageTypeManager(TestMessageType.CLIENT);
        Server<TestRemoteClient> server = new Server<>(typeManager, new RemoteClientFactory<TestRemoteClient>()
        {
            @Override
            public TestRemoteClient create(Server<TestRemoteClient> server, Socket socket) throws IOException
            {
                return new TestRemoteClient(server, socket);
            }
        }, port);
        server.start();
        return server;
    }

    private TestClient startClient(int port, int localPort)
    {
        MessageTypeManager typeManager = new MessageTypeManager(TestMessageType.CLIENT);
        TestClient client = new TestClient(typeManager, "127.0.0.1", port, localPort);
        client.start();
        return client;
    }

    private static int generatePort()
    {
        synchronized (PORTS)
        {
            int port = 8080;
            while (PORTS.contains(port))
            {
                port++;
            }
            PORTS.add(port);
            return port;
        }
    }

    private void assertDisconnected(Server server, TestRemoteClient remoteClient, TestClient client)
    {
        Util.sleep(100);

        Assert.assertTrue(server.getClients().isEmpty());
        Assert.assertTrue(remoteClient.disconnected);
        Assert.assertTrue(client.disconnected);
    }

    private class TestRemoteClient extends RemoteClient<TestRemoteClient>
    {
        private boolean receivedMessage = false;

        private boolean connected = false;

        private boolean disconnected = false;

        public TestRemoteClient(Server<TestRemoteClient> server, Socket socket) throws IOException
        {
            super(server, socket);
            connected = true;
        }

        @Override
        protected void receive(Message message)
        {
            receivedMessage = true;
            send(message);
        }

        @Override
        protected void disconnected()
        {
            disconnected = true;
        }
    }

    private class TestClient extends Client
    {
        private boolean receivedMessage = false;

        private boolean connected = false;

        private boolean disconnected = false;

        public TestClient(MessageTypeManager typeManager, String address, int port, int localPort)
        {
            super(typeManager, address, port, localPort);
            connected = true;
        }

        @Override
        protected void receive(Message message)
        {
            receivedMessage = true;
        }

        @Override
        protected void disconnected()
        {
            disconnected = true;
        }
    }
}
