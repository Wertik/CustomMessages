package space.devport.wertik.custommessages.system.struct;

public enum MessageType {

    JOIN, LEAVE;

    public static MessageType fromString(String str) {
        try {
            return MessageType.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}