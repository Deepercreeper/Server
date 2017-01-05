package org.deepercreeper.server;

import org.deepercreeper.common.util.IOUtil;
import org.deepercreeper.messages.Message;
import org.deepercreeper.messages.MessageTypeManager;

class MessageListener implements Runnable
{
    private final RemoteClient remoteClient;

    private final MessageTypeManager typeManager;

    MessageListener(RemoteClient remoteClient, MessageTypeManager typeManager)
    {
        this.remoteClient = remoteClient;
        this.typeManager = typeManager;
    }

    @Override
    public void run()
    {
        while (remoteClient.isRunning() && remoteClient.isConnected())
        {
            receive();
        }
    }

    private void receive()
    {
        String line = readLine();
        if (line != null)
        {
            receive(line);
        }
        else
        {
            remoteClient.close();
        }
    }

    private void receive(String line)
    {
        try
        {
            remoteClient.receive(Message.decode(line, typeManager));
        }
        catch (Exception e)
        {
            new RuntimeException("Client sent a invalid message: " + remoteClient.getAddress(), e).printStackTrace();
        }
    }

    private String readLine()
    {
        return IOUtil.readSocketMessage(remoteClient.getIn());
    }
}
