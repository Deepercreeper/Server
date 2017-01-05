package org.deepercreeper.server;

import org.deepercreeper.common.data.Bundle;
import org.deepercreeper.messages.Message;
import org.deepercreeper.messages.TestMessageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.Socket;

public class FrameRemoteClient extends RemoteClient<FrameRemoteClient>
{
    public static RemoteClientFactory<FrameRemoteClient> FACTORY = new RemoteClientFactory<FrameRemoteClient>()
    {
        @Override
        public FrameRemoteClient create(Server<FrameRemoteClient> server, Socket socket) throws IOException
        {
            return new FrameRemoteClient(server, socket);
        }
    };

    private final JFrame frame = new JFrame("Server");

    public FrameRemoteClient(Server<FrameRemoteClient> server, Socket socket) throws IOException
    {
        super(server, socket);
        System.out.println("Connected");
        init();
    }

    private void init()
    {
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
                    Message message = new Message(TestMessageType.SERVER, new Bundle().put("message", field.getText()));
                    send(message);
                    System.out.println("Sent: " + message);
                    field.setText("");
                }
            }
        });
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    protected void receive(Message message)
    {
        System.out.println("Received: " + message);
        frame.setVisible(true);
    }

    @Override
    protected void disconnected()
    {
        System.out.println("Disconnected");
    }
}
