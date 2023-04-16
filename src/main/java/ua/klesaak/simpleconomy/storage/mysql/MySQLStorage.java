package ua.klesaak.simpleconomy.storage.mysql;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.Bukkit;
import ua.klesaak.simpleconomy.manager.PlayerData;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.storage.IStorage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MySQLStorage implements IStorage {
    ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(3);
    Cache<String, AsyncUpdateContainer<PlayerData>> temporalCache = CacheBuilder.newBuilder()
            .initialCapacity(Bukkit.getMaxPlayers())
            .concurrencyLevel(16)
            .expireAfterWrite(120, TimeUnit.SECONDS).build(); //Временный кеш, чтобы уменьшить кол-во запросов в бд при оффлайн игроке.
    Map<String, AsyncUpdateContainer<PlayerData>> playersCache = new ConcurrentHashMap<>(Bukkit.getMaxPlayers());
    JdbcPooledConnectionSource connectionSource;
    Dao<PlayerData, String> playerDataDao;
    SimpleEconomyManager manager;

    public MySQLStorage(SimpleEconomyManager manager) {
        this.manager = manager;
        val config = new MySQLConfig(manager.getConfigFile().getMySQLSection());
        try {
            List<DatabaseFieldConfig> fieldConfigs = new ArrayList<>();
            DatabaseFieldConfig playerName = new DatabaseFieldConfig("playerName");
            playerName.setId(true);
            playerName.setCanBeNull(false);
            playerName.setColumnName("player_name");
            fieldConfigs.add(playerName);
            DatabaseFieldConfig moneyField = new DatabaseFieldConfig("money");
            moneyField.setCanBeNull(false);
            moneyField.setDataType(DataType.DOUBLE);
            fieldConfigs.add(moneyField);
            DatabaseFieldConfig coinsField = new DatabaseFieldConfig("coins");
            moneyField.setDataType(DataType.BIG_INTEGER);
            moneyField.setCanBeNull(false);
            fieldConfigs.add(coinsField);
            DatabaseTableConfig<PlayerData> accountTableConfig = new DatabaseTableConfig<>(PlayerData.class, config.getTable(), fieldConfigs);
            this.connectionSource = new JdbcPooledConnectionSource(config.getHost());
            this.playerDataDao = DaoManager.createDao(this.connectionSource, accountTableConfig);
            TableUtils.createTableIfNotExists(this.connectionSource, accountTableConfig);
        } catch (SQLException ex) {
            throw new RuntimeException("Произошла ошибка при инициализации MySQL", ex);
        }
        this.connectionSource.setTestBeforeGet(true);
    }

    @Override
    public void init() {}

    @Override
    public void savePlayer(String nickName, PlayerData playerData) {

    }

    @Override
    public void cachePlayer(String nickName) {

    }

    @Override
    public void unCachePlayer(String nickName) {

    }

    @Override
    public boolean hasAccount(String nickName) {
        return false;
    }

    @Override
    public double getMoneyBalance(String nickName) {
        return 0;
    }

    @Override
    public boolean hasMoney(String nickName, double amount) {
        return false;
    }

    @Override
    public boolean withdrawMoney(String nickName, double amount) {
        return false;
    }

    @Override
    public boolean depositMoney(String nickName, double amount) {
        return false;
    }

    @Override
    public int getCoinsBalance(String nickName) {
        return 0;
    }

    @Override
    public boolean hasCoins(String nickName, int amount) {
        return false;
    }

    @Override
    public boolean withdrawCoins(String nickName, int amount) {
        return false;
    }

    @Override
    public boolean depositCoins(String nickName, int amount) {
        return false;
    }

    @Override
    public boolean createAccount(String nickName) {
        return false;
    }

    @Override
    public PlayerData getPlayer(String nickName) {
        return null;
    }

    @Override
    public Collection<PlayerData> getMoneyTop(int amount) {
        return null;
    }

    @Override
    public Collection<PlayerData> getCoinsTop(int amount) {
        return null;
    }

    @Override @SneakyThrows
    public void close() {
        scheduledExecutor.shutdown();
        scheduledExecutor.awaitTermination(10, TimeUnit.MINUTES);
        this.connectionSource.closeQuietly();
    }
}
