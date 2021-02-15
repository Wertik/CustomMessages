package space.devport.wertik.custommessages.sounds;

import lombok.extern.java.Log;
import org.bukkit.entity.Player;
import space.devport.dock.configuration.Configuration;
import space.devport.wertik.custommessages.MessagePlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log
public class SoundRegistry {

    private final MessagePlugin plugin;

    private final Map<SoundType, ConfiguredSound> sounds = new HashMap<>();

    private final Configuration config;

    public SoundRegistry(MessagePlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin, "sounds");
    }

    public void load() {
        sounds.clear();
        config.load();

        for (SoundType type : SoundType.VALUES) {
            String path = type.getPath();
            ConfiguredSound sound = ConfiguredSound.load(config, path);

            if (sound == null)
                continue;

            sounds.put(type, sound);
        }
        log.info(() -> "Loaded " + sounds.size() + " sound(s)...");
    }

    public Optional<ConfiguredSound> get(SoundType type) {
        return Optional.ofNullable(sounds.get(type));
    }

    public void play(Player player, SoundType type) {
        get(type).ifPresent(s -> s.play(player));
    }
}
