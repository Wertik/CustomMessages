package space.devport.wertik.custommessages.system.struct;

import space.devport.utils.text.message.Message;

public interface ExtraParser {

    // Apply extra to the output message.
    Message parse(Message message, Object[] extra);
}
