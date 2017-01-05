package org.deepercreeper.messages;

import org.deepercreeper.common.encoding.Encodable;

public interface MessageType extends Encodable
{
    String getIdentifier();
}
