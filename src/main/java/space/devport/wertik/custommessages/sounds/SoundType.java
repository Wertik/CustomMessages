package space.devport.wertik.custommessages.sounds;

import lombok.Getter;

public enum SoundType {

    MENU_OPEN("menu.open"),
    MENU_CLOSE("menu.close"),
    MENU_PICK("menu.pick"),
    MENU_USED("menu.already-used"),

    MESSAGE_JOIN("message.join"),
    MESSAGE_LEAVE("message.leave"),
    MESSAGE_KILL("message.kill");

    public static final SoundType[] VALUES = values();

    @Getter
    private final String path;

    SoundType(String path) {
        this.path = path;
    }
}
