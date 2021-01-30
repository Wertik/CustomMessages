package space.devport.wertik.custommessages.system.message.type;

import java.util.Map;

public interface DefaultParser {

    String parse(String message, Map<String, String> defaults);
}
