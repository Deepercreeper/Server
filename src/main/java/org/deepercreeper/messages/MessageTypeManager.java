package org.deepercreeper.messages;

import org.deepercreeper.common.util.ConditionUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MessageTypeManager
{
    private final Map<String, MessageType> types = new HashMap<>();

    public MessageTypeManager(MessageType... types)
    {
        add(types);
    }

    public void add(Collection<? extends MessageType> types)
    {
        if (types == null || types.isEmpty())
        {
            return;
        }
        for (MessageType type : types)
        {
            add(type);
        }
    }

    public void add(MessageType... types)
    {
        if (types == null || types.length == 0)
        {
            return;
        }
        for (MessageType type : types)
        {
            add(type);
        }
    }

    private void add(MessageType type)
    {
        String identifier = type.getIdentifier();
        ConditionUtil.checkNotNull(identifier, "Message type identifier");
        if (types.containsKey(identifier))
        {
            throw new IllegalArgumentException("Message type identifier is already in use: \"" + identifier + "\"");
        }
        types.put(identifier, type);
    }

    public MessageType get(String identifier)
    {
        return types.get(identifier);
    }
}
