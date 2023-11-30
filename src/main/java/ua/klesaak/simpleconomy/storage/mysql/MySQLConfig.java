package ua.klesaak.simpleconomy.storage.mysql;

import lombok.Getter;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;
import ua.klesaak.simpleconomy.storage.StorageType;

@Getter
public class MySQLConfig {
    private final String port;
    private final String username, password, database, address, table;
    private final boolean isUseSSL;

    public MySQLConfig(ConfigurationSection section) {
        this.address = section.getString("host");
        this.port = String.valueOf(section.getInt("port"));
        this.username = section.getString("username");
        this.password = section.getString("password");
        this.database = section.getString("database");
        this.table = section.getString("table");
        this.isUseSSL = section.getBoolean("useSSL");
    }
}