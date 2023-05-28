package ua.klesaak.simpleconomy.storage.mysql;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Bukkit;
import ua.klesaak.simpleconomy.manager.PlayerData;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.storage.IStorage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MySQLStorage implements IStorage {
    private final Cache<String, AsyncUpdateContainer<PlayerData>> temporalCache = CacheBuilder.newBuilder()
            .initialCapacity(Bukkit.getMaxPlayers())
            .concurrencyLevel(16)
            .expireAfterWrite(1, TimeUnit.MINUTES).build(); //Временный кеш, чтобы уменьшить кол-во запросов в бд.
    private final JdbcPooledConnectionSource connectionSource;
    private final Dao<PlayerData, String> playerDataDao;
    private final SimpleEconomyManager manager;

    public MySQLStorage(SimpleEconomyManager manager) {
        this.manager = manager;
        val config = new MySQLConfig(manager.getConfigFile().getMySQLSection());
        try {
            List<DatabaseFieldConfig> fieldConfigs = new ArrayList<>();
            DatabaseFieldConfig playerName = new DatabaseFieldConfig("playerName");
            playerName.setId(true);
            playerName.setCanBeNull(false);
            playerName.setColumnName(MySQLConfig.PLAYER_COLUMN);
            fieldConfigs.add(playerName);
            DatabaseFieldConfig moneyField = new DatabaseFieldConfig(MySQLConfig.MONEY_COLUMN);
            moneyField.setCanBeNull(false);
            moneyField.setDataType(DataType.DOUBLE);
            moneyField.setDefaultValue("0");
            fieldConfigs.add(moneyField);
            DatabaseFieldConfig coinsField = new DatabaseFieldConfig(MySQLConfig.COINS_COLUMN);
            coinsField.setDataType(DataType.INTEGER);
            coinsField.setCanBeNull(false);
            coinsField.setDefaultValue("0");
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
        PlayerData playerData = this.loadPlayer(nickName);
        val configFile = manager.getConfigFile();
        if (playerData == null) {
            playerData = new PlayerData(nickName, configFile.getStartBalance(), configFile.getStartCoins());
        }
        AsyncUpdateContainer<PlayerData> container = new AsyncUpdateContainer<>(this.playerDataDao, playerData);
        this.temporalCache.put(nickName, container);
        return container;
    }

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

    @Override
    public boolean hasAccount(String nickName) {
        val temporalContainer = this.temporalCache.getIfPresent(nickName);
        if (temporalContainer != null) return true;
        PlayerData playerData = this.loadPlayer(nickName);
        return playerData != null;
    }

    private PlayerData loadPlayer(String nickName) {
        return CompletableFuture.supplyAsync(()-> {
            try {
                return this.playerDataDao.queryForId(nickName);
            } catch (SQLException e) {
                throw new RuntimeException("Error while load player " + nickName, e);
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        }).join();
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
    public boolean setMoney(String nickName, double amount) {
        val container = this.getPlayerContainer(nickName);
        container.getObject().setMoney(amount);
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
    public boolean setCoins(String nickName, int amount) {
        val container = this.getPlayerContainer(nickName);
        container.getObject().setCoins(amount);
        container.scheduleUpdate();
        return true;
    }

    @Override
    public boolean createAccount(String nickName) {
        System.out.println("S-ECON-DEBUG: MySQLStorage.class, createAccount method has been called.");
        return true;
    }

    @Override
    public PlayerData getPlayer(String nickName) {
        return this.getPlayerContainer(nickName).getObject();
    }

    @Override
    public void deleteAccount(String nickName) {
        CompletableFuture.runAsync(() -> {
            try {
                this.playerDataDao.deleteById(nickName);
            } catch (SQLException ex) {
                throw new RuntimeException("Произошла ошибка при удалении данных из MySQL", ex);
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Override @SneakyThrows(SQLException.class)
    public List<String> getMoneyTop(int amount) {
        List<String> list = new ArrayList<>(128);
        QueryBuilder<PlayerData, String> queryBuilder = this.playerDataDao.queryBuilder();
        queryBuilder.selectColumns(MySQLConfig.PLAYER_COLUMN, MySQLConfig.MONEY_COLUMN).orderBy(MySQLConfig.MONEY_COLUMN, false).limit((long)amount);
        List<PlayerData> rs = queryBuilder.query();
        for (val data : rs) {
            list.add(this.manager.getConfigFile().formatTopLine(String.valueOf(list.size()+1), data.getPlayerName(), String.valueOf(data.getMoney())));
        }
        return list;
    }

    @Override @SneakyThrows(SQLException.class)
    public List<String> getCoinsTop(int amount) {
        List<String> list = new ArrayList<>(128);
        QueryBuilder<PlayerData, String> queryBuilder = this.playerDataDao.queryBuilder();
        queryBuilder.selectColumns(MySQLConfig.PLAYER_COLUMN, MySQLConfig.COINS_COLUMN).orderBy(MySQLConfig.COINS_COLUMN, false).limit((long)amount);
        List<PlayerData> rs = queryBuilder.query();
        for (val data : rs) {
            list.add(this.manager.getConfigFile().formatTopLine(String.valueOf(list.size()+1), data.getPlayerName(), String.valueOf(data.getCoins())));
        }
        return list;
    }

    @Override
    public void close() {
        this.connectionSource.closeQuietly();
    }
}
