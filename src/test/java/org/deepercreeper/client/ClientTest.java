package org.deepercreeper.client;

import org.deepercreeper.common.data.Bundle;
import org.deepercreeper.common.util.Util;
import org.deepercreeper.messages.Message;
import org.deepercreeper.messages.MessageTypeManager;
import org.deepercreeper.messages.TestMessageType;
import org.junit.Ignore;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ClientTest
{
    @Test
    @Ignore
    public void testClient()
    {
        String address = "127.0.0.1";
        int port = 8080;
        int localPort = 8081;

        MessageTypeManager typeManager = new MessageTypeManager();
        typeManager.add(TestMessageType.CLIENT);
        Client client = new TestClient(typeManager, address, port, localPort);
        client.start();

        listen(client);

        client.stop();
    }

    private void listen(final Client client)
    {
        JFrame frame = new JFrame("Client");
        frame.setLayout(new BorderLayout());
        final JTextField field = new JTextField();
        frame.add(field);
        field.setPreferredSize(new Dimension(500, 24));
        field.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER)
                {
                    Message message = new Message(TestMessageType.CLIENT, new Bundle().put("message", field.getText()));
                    client.send(message);
                    System.out.println("Sent: " + message);
                    field.setText("");
                }
            }
        });
        frame.pack();
        frame.setVisible(true);
        while (frame.isVisible())
        {
            Util.sleep(100);
        }
    }

    private class TestClient extends Client
    {
        public TestClient(MessageTypeManager typeManager, String address, int port, int localPort)
        {
            super(typeManager, address, port, localPort);
        }

        public TestClient(MessageTypeManager typeManager, String address, int port)
        {
            super(typeManager, address, port);
        }

        @Override
        protected void receive(Message message)
        {
            System.out.println("Received: " + message);
        }

        @Override
        protected void disconnected()
        {
            System.out.println("Disconnected");
        }
    }
}
