package ua.klesaak.simpleconomy.storage.sql;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class SQLConfig {
    private final String port;
    private final String username, password, database, address, table;
    private final boolean isUseSSL;

    public SQLConfig(ConfigurationSection section) {
        this.address = section.getString("host");
        this.port = String.valueOf(section.getInt("port"));
        this.username = section.getString("username");
        this.password = section.getString("password");
        this.database = section.getString("database");
        this.table = section.getString("table");
        this.isUseSSL = section.getBoolean("useSSL");
    }
}