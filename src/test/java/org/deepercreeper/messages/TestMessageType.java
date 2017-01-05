package org.deepercreeper.messages;

public class TestMessageType
{
    public static final MessageType SERVER = new DefaultMessageType("Server");

    public static final MessageType CLIENT = new DefaultMessageType("Client");

    private TestMessageType()
    {
    }
}
