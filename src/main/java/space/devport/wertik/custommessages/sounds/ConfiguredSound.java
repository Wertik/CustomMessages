package space.devport.wertik.custommessages.sounds;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.configuration.Configuration;
import space.devport.dock.lib.xseries.XSound;
import space.devport.dock.util.LocationUtil;
import space.devport.dock.util.ParseUtil;

import java.util.Optional;

@Log
public class ConfiguredSound {

    @Getter
    private final XSound type;
    @Getter
    private final SoundPlayType playType;
    @Getter
    private int volume;
    @Getter
    private int pitch;

    public ConfiguredSound(XSound type, SoundPlayType playType) {
        this.type = type;
        this.playType = playType;
    }

    public static ConfiguredSound load(Configuration config, String path) {
        ConfigurationSection section = config.getFileConfiguration().getConfigurationSection(path);

        if (section == null)
            return null;

        String typeString = section.getString("type");

        if (Strings.isNullOrEmpty(typeString)) {
            log.fine(() -> "Loaded no sound at " + config.composePath(path));
            return null;
        }

        Optional<XSound> sound = XSound.matchXSound(typeString);

        if (!sound.isPresent()) {
            log.warning(() -> "Invalid sound " + typeString + " at " + config.composePath(path));
            return null;
        }

        SoundPlayType playType = ParseUtil.parseEnumHandled(section.getString("play-type", "PLAYER"), SoundPlayType.class,
                e -> log.warning(() -> "Invalid sound play type " + e.getInput() + " at " + config.composePath(path + ".play-type")));

        if (playType == null)
            return null;

        ConfiguredSound configuredSound = new ConfiguredSound(sound.get(), playType);

        configuredSound.volume = section.getInt("volume", 1);
        configuredSound.pitch = section.getInt("pitch", 1);

        log.fine(() -> "Loaded sound " + configuredSound.toString());
        return configuredSound;
    }

    public void play(Player player) {
        switch (playType) {
            case LOCAL:
                playLocal(player.getLocation());
                break;
            case GLOBAL:
                playGlobal();
                break;
            default:
            case PLAYER:
                playForPlayer(player);
        }
    }

    public void playLocal(@NotNull Location location) {
        type.play(location, volume, pitch);
        log.fine(() -> "Played sound " + toString() + " at " + LocationUtil.composeString(location));
    }

    public void playForPlayer(@NotNull Player player) {
        type.play(player, volume, pitch);
        log.fine(() -> "Player sound " + toString() + " for " + player.getName());
    }

    public void playGlobal() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            play(player);
        }
        log.fine(() -> "Played sound " + toString() + " for all players");
    }

    @Override
    public String toString() {
        return String.format("ConfiguredSound{type=%s,playType=%s,volume=%d,pitch=%d}",
                type.toString(), playType.toString(), volume, pitch);
    }
}
