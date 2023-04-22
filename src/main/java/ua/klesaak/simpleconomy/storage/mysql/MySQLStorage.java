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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MySQLStorage implements IStorage {
    ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(3);
    Cache<String, AsyncUpdateContainer<PlayerData>> temporalCache = CacheBuilder.newBuilder()
            .initialCapacity(Bukkit.getMaxPlayers())
            .concurrencyLevel(16)
            .expireAfterWrite(1, TimeUnit.MINUTES).build(); //Временный кеш, чтобы уменьшить кол-во запросов в бд.
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
            coinsField.setDataType(DataType.INTEGER);
            coinsField.setCanBeNull(false);
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

    @SneakyThrows(SQLException.class)
    private AsyncUpdateContainer<PlayerData> getPlayerContainer(String nickName) {
        val temporalContainer = this.temporalCache.getIfPresent(nickName);
        if (temporalContainer != null) return temporalContainer;
        PlayerData playerData = this.playerDataDao.queryForId(nickName);
        if (playerData == null) {
            playerData = new PlayerData(nickName, manager.getConfigFile().getStartBalance(), manager.getConfigFile().getStartCoins());
        }
        AsyncUpdateContainer<PlayerData> container = new AsyncUpdateContainer<>(this.playerDataDao, this.scheduledExecutor, playerData);
        this.temporalCache.put(nickName, container);
        return container;
    }

    @Override
    public void init() {}

    @Override
    public void savePlayer(String nickName, PlayerData playerData) {
        AsyncUpdateContainer<PlayerData> container = this.temporalCache.getIfPresent(nickName);
        if (container != null) container.scheduleUpdate();
    }

    @Override
    public void cachePlayer(String nickName) {
        this.getPlayer(nickName);
    }

    @Override
    public void unCachePlayer(String nickName) {
        this.temporalCache.invalidate(nickName);
    }

    @Override @SneakyThrows(SQLException.class)
    public boolean hasAccount(String nickName) {
        val temporalContainer = this.temporalCache.getIfPresent(nickName);
        if (temporalContainer != null) return true;
        PlayerData playerData = this.playerDataDao.queryForId(nickName);
        return playerData != null;
    }

    @Override
    public double getMoneyBalance(String nickName) {
        return this.getPlayer(nickName).getMoney();
    }

    @Override
    public boolean hasMoney(String nickName, double amount) {
        return this.getPlayer(nickName).getMoney() >= amount;
    }

    @Override
    public boolean withdrawMoney(String nickName, double amount) {
        val container = this.getPlayerContainer(nickName);
        container.getObject().withdrawMoney(amount);
        container.scheduleUpdate();
        return true;
    }

    @Override
    public boolean depositMoney(String nickName, double amount) {
        val container = this.getPlayerContainer(nickName);
        container.getObject().depositMoney(amount);
        container.scheduleUpdate();
        return true;
    }

    @Override
    public int getCoinsBalance(String nickName) {
        return this.getPlayer(nickName).getCoins();
    }

    @Override
    public boolean hasCoins(String nickName, int amount) {
        return this.getPlayer(nickName).getCoins() >= amount;
    }

    @Override
    public boolean withdrawCoins(String nickName, int amount) {
        val container = this.getPlayerContainer(nickName);
        container.getObject().withdrawCoins(amount);
        container.scheduleUpdate();
        return true;
    }

    @Override
    public boolean depositCoins(String nickName, int amount) {
        val container = this.getPlayerContainer(nickName);
        container.getObject().depositCoins(amount);
        container.scheduleUpdate();
        return true;
    }

    @Override
    public boolean createAccount(String nickName) {
        System.out.println("S-ECON-DEBUG: MySQLStorage.class, createAccount method has been called.");
        return true;
    }

    @Override @SneakyThrows(SQLException.class)
    public PlayerData getPlayer(String nickName) {
        val temporalContainer = this.temporalCache.getIfPresent(nickName);
        if (temporalContainer != null) return temporalContainer.getObject();
        PlayerData playerData = this.playerDataDao.queryForId(nickName);
        if (playerData == null) {
            playerData = new PlayerData(nickName, manager.getConfigFile().getStartBalance(), manager.getConfigFile().getStartCoins());
        }
        this.temporalCache.put(nickName, new AsyncUpdateContainer<>(this.playerDataDao, this.scheduledExecutor, playerData));
        return playerData;
    }

    @Override
    public Collection<String> getMoneyTop(int amount) {
        return null;
    }

    @Override
    public Collection<String> getCoinsTop(int amount) {
        return null;
    }

    @Override @SneakyThrows
    public void close() {
        scheduledExecutor.shutdown();
        scheduledExecutor.awaitTermination(10, TimeUnit.MINUTES);
        this.connectionSource.closeQuietly();
    }
}
