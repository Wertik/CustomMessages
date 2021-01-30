package space.devport.wertik.custommessages.storage.mysql;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.configuration.Configuration;

public class ConnectionInfo {

    @Getter
    private final String host;
    @Getter
    private final int port;
    @Getter
    private final String username;
    @Getter
    private final String password;
    @Getter
    private final String database;
    @Getter
    @Setter
    private boolean readOnly = false;

    public ConnectionInfo(String host, int port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public ConnectionInfo(ConnectionInfo info) {
        this.host = info.getHost();
        this.port = info.getPort();
        this.username = info.getUsername();
        this.password = info.getPassword();
        this.database = info.getDatabase();
    }

    @Nullable
    public static ConnectionInfo load(ConfigurationSection config) {
        if (config == null)
            return null;

        return new ConnectionInfo(config.getString("host", "localhost"),
                config.getInt("port", 3306),
                config.getString("user", "root"),
                config.getString("pass", "secretpassword"),
                config.getString("database", "custommessages"));
    }

    @Override
    public String toString() {
        return username + "@" + host + ":" + port + "/" + database + " -p " + beepOut(password);
    }

    private String beepOut(String input) {
        StringBuilder str = new StringBuilder();
        for (int n = 0; n < input.length(); n++)
            str.append("*");
        return str.toString();
    }

    /*
     * Don't override #equals().
     */
    public boolean compare(ConnectionInfo info) {
        return this.host.equals(info.getHost()) &&
                this.port == info.getPort() &&
                this.username.equals(info.getUsername()) &&
                this.database.equals(info.getDatabase());
    }
}
