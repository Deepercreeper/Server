package org.deepercreeper.messages;

import org.deepercreeper.common.util.CodingUtil;
import org.deepercreeper.common.util.ConditionUtil;

public class DefaultMessageType implements MessageType
{
    private final String identifier;

    public DefaultMessageType(String identifier)
    {
        ConditionUtil.checkNotNull(identifier, "Identifier");
        this.identifier = identifier;
    }

    public final String getIdentifier()
    {
        return identifier;
    }

    @Override
    public String encode()
    {
        return CodingUtil.encode(getIdentifier());
    }
}
