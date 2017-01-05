package org.deepercreeper.messages;

import org.deepercreeper.common.data.Bundle;
import org.deepercreeper.common.util.CodingUtil;
import org.deepercreeper.common.util.ConditionUtil;

import java.util.Objects;

public class Message
{
    private final Bundle bundle;

    private final MessageType type;

    public Message(MessageType type)
    {
        this(type, new Bundle());
    }

    public Message(MessageType type, Bundle bundle)
    {
        ConditionUtil.checkNotNull(type, "Type");
        ConditionUtil.checkNotNull(bundle, "Bundle");
        this.type = type;
        this.bundle = bundle;
    }

    private Message(String message, MessageTypeManager typeManager)
    {
        int delimiterIndex = message.indexOf(CodingUtil.DELIMITER);
        type = decodeType(message.substring(0, delimiterIndex), typeManager);
        bundle = Bundle.decode(message.substring(delimiterIndex + 1));
    }

    private MessageType decodeType(String type, MessageTypeManager typeManager)
    {
        return typeManager.get(CodingUtil.decode(type));
    }

    public Bundle getBundle()
    {
        return bundle;
    }

    public MessageType getType()
    {
        return type;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Message)
        {
            Message message = (Message) obj;
            return getType().equals(message.getType()) && getBundle().equals(message.getBundle());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getType(), getBundle());
    }

    public String encode()
    {
        return CodingUtil.encode(getType(), getBundle());
    }

    @Override
    public String toString()
    {
        return getType().getIdentifier() + ": " + getBundle();
    }

    public static Message decode(String value, MessageTypeManager typeManager)
    {
        try
        {
            return tryDecode(value, typeManager);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Could not decode message: " + value, e);
        }
    }

    private static Message tryDecode(String value, MessageTypeManager typeManager)
    {
        String[] values = CodingUtil.split(CodingUtil.decode(value));
        MessageType type = typeManager.get(CodingUtil.decode(values[0]));
        Bundle bundle = values.length > 1 ? Bundle.decode(values[1]) : new Bundle();
        return new Message(type, bundle);
    }
}
