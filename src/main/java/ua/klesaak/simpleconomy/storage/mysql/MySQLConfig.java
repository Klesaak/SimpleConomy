package ua.klesaak.simpleconomy.storage.mysql;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
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
        val builder = new StringBuilder("jdbc:mysql://");
        builder.append(this.username);
        builder.append(":");
        builder.append(this.password);
        builder.append("@");
        builder.append(this.address);
        builder.append("/");
        builder.append(this.database);
        builder.append("?useUnicode=true&");
        builder.append("characterEncoding=utf-8&");
        builder.append("prepStmtCacheSize=250&");
        builder.append("prepStmtCacheSqlLimit=2048&");
        builder.append("cachePrepStmts=true&");
        builder.append("useServerPrepStmts=true&");
        builder.append("cacheServerConfiguration=true&");
        builder.append("useLocalSessionState=true&");
        builder.append("rewriteBatchedStatements=true&");
        builder.append("maintainTimeStats=false&");
        builder.append("useUnbufferedInput=false&");
        builder.append("useReadAheadInput=false&");
        builder.append("useSSL=");
        builder.append(this.isUseSSL);
        builder.append("&autoReconnect=true");
        return builder.toString();
    }
}