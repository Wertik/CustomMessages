package space.devport.wertik.custommessages.system.message.type;

import space.devport.utils.text.message.Message;

public interface ExtraParser {

    // Apply extra to the output message.
    Message parse(Message message, Object[] extra);
}
