package org.deepercreeper.client;

import org.deepercreeper.common.util.IOUtil;
import org.deepercreeper.messages.Message;
import org.deepercreeper.messages.MessageTypeManager;

class MessageListener implements Runnable
{
    private final Client client;

    private final MessageTypeManager typeManager;

    MessageListener(Client client, MessageTypeManager typeManager)
    {
        this.client = client;
        this.typeManager = typeManager;
    }

    @Override
    public void run()
    {
        while (client.isRunning() && client.isConnected())
        {
            receive();
        }
    }

    private void receive()
    {
        String line = readLine();
        if (!client.isRunning())
        {
            return;
        }
        if (line != null)
        {
            receive(line);
        }
        else
        {
            client.close();
        }
    }

    private void receive(String line)
    {
        try
        {
            client.receive(Message.decode(line, typeManager));
        }
        catch (Exception e)
        {
            new RuntimeException("Server sent a invalid message: " + client.getAddress(), e).printStackTrace();
        }
    }

    private String readLine()
    {
        return IOUtil.readSocketMessage(client.getIn());
    }
}
