package net.klesaak.simpleconomy.configurations;

import com.typesafe.config.Config;
import lombok.Getter;
import net.klesaak.storagelib.api.NumberUtils;
import net.klesaak.storagelib.api.hocon.HoconConfig;
import net.klesaak.storagelib.pool.StoragePoolManager;
import net.klesaak.storagelib.pool.redis.RedisPool;
import net.klesaak.storagelib.pool.sql.SQLPool;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ConfigFile extends HoconConfig {
    private static ConfigFile IMP;

    private final boolean payCommandEnabled;
    private final String sqlTable;
    private final String sqlPoolKeyId;
    private final String redisPoolKeyId;
    private final int maxBalance, maxCoins, minTransactionSum;
    private final String currencyFormatPlural, currencyFormatSingular, currencyFormatPlural2, currencyCoinsFormatPlural, currencyCoinsFormatSingular, currencyCoinsFormatPlural2;

    private ConfigFile(JavaPlugin plugin) {
        super(plugin.getDataFolder(), "settings.conf");
        this.payCommandEnabled = this.handle.getBoolean("payCommandEnabled");
        Config sqlConf = this.handle.getConfig("sqlSettings");
        this.sqlPoolKeyId = sqlConf.getString("poolKeyId");
        this.sqlTable = sqlConf.getString("table");
        this.redisPoolKeyId = this.handle.getString("redisPoolKeyId");
        this.maxBalance = this.handle.getInt("maxBalance");
        this.maxCoins = this.handle.getInt("maxCoins");
        this.minTransactionSum = this.handle.getInt("minTransactionSum");
        this.currencyFormatPlural = this.handle.getString("currencyFormat.plural");
        Config currencyFormat = this.handle.getConfig("currencyFormat");
        this.currencyFormatSingular = currencyFormat.getString("singular");
        this.currencyFormatPlural2 = currencyFormat.getString("plural2");
        Config currencyCoinsFormat = this.handle.getConfig("currencyCoinsFormat");
        this.currencyCoinsFormatPlural = currencyCoinsFormat.getString("plural");
        this.currencyCoinsFormatSingular = currencyCoinsFormat.getString("singular");
        this.currencyCoinsFormatPlural2 = currencyCoinsFormat.getString("plural2");
    }

    public SQLPool getSQLPool() {
        return StoragePoolManager.get().getSQLPool(this.sqlPoolKeyId);
    }

    public RedisPool getRedisPool() {
        return StoragePoolManager.get().getRedisPool(this.redisPoolKeyId);
    }

    public String formatMoney(double amount) {
        return NumberUtils.formatting((int)amount, this.currencyFormatSingular, this.currencyFormatPlural, this.currencyFormatPlural2);
    }

    public String formatCoins(int amount) {
        return NumberUtils.formatting(amount, this.currencyCoinsFormatSingular, this.currencyCoinsFormatPlural, this.currencyCoinsFormatPlural2);
    }

    public static ConfigFile get() {
        return IMP;
    }

    public static void reload(JavaPlugin plugin) {
        IMP = new ConfigFile(plugin);
    }
}
