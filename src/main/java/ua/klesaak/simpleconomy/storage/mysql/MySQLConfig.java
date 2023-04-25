package ua.klesaak.simpleconomy.storage.mysql;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.ConfigurationSection;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MySQLConfig {
    public static final String PLAYER_COLUMN = "player_name";
    public static final String MONEY_COLUMN = "money";
    public static final String COINS_COLUMN = "coins";
    int port;
    String username, password, database, address, table;
    boolean isUseSSL;

    public MySQLConfig(ConfigurationSection section) {
        this.address = section.getString("host");
        this.port = section.getInt("port");
        this.username = section.getString("username");
        this.password = section.getString("password");
        this.database = section.getString("database");
        this.table = section.getString("table");
        this.isUseSSL = section.getBoolean("useSSL");
    }

    public String getHost() {
        return "jdbc:mysql://" + this.username + ":" + this.password + "@" + this.address + "/" + this.database +
                "?useUnicode=true&" +
                "characterEncoding=utf-8&" +
                "prepStmtCacheSize=250&" +
                "prepStmtCacheSqlLimit=2048&" +
                "cachePrepStmts=true&" +
                "useServerPrepStmts=true&" +
                "cacheServerConfiguration=true&" +
                "useLocalSessionState=true&" +
                "rewriteBatchedStatements=true&" +
                "maintainTimeStats=false&" +
                "useUnbufferedInput=false&" +
                "useReadAheadInput=false&" +
                "useSSL=" + this.isUseSSL +
                "&autoReconnect=true";
    }
}