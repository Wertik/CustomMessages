package space.devport.wertik.custommessages.system.struct;

import org.jetbrains.annotations.Nullable;

public enum MessageType {

    JOIN,
    LEAVE,
    KICK;

    @Nullable
    public static MessageType fromString(String str) {
        try {
            return MessageType.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}