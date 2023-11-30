package ua.klesaak.simpleconomy.storage.mysql;

import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.val;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.storage.AbstractStorage;
import ua.klesaak.simpleconomy.storage.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MySQLStorage extends AbstractStorage {
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS {TABLE} (playerName VARCHAR(16) NOT NULL UNIQUE, money BIGINT DEFAULT 0 , coins BIGINT DEFAULT 0 ) ENGINE = InnoDB; ";
    public static final String INSERT_PLAYER = "INSERT INTO {TABLE} (playerName, money, coins) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE playerName=?, money=?, coins=?"; //executeUpdate()
    public static final String GET_PLAYER_DATA = "SELECT money, coins FROM {TABLE} WHERE playerName=?"; //executeQuery()
    public static final String GET_MONEY_TOP = "SELECT playerName, money FROM {TABLE} ORDER BY money DESC LIMIT {COUNT_IN_TOP}"; //executeQuery()
    public static final String GET_COINS_TOP = "SELECT playerName, coins FROM {TABLE} ORDER BY coins DESC LIMIT {COUNT_IN_TOP}"; //executeQuery()
    public static final String DELETE_PLAYER = "DELETE FROM {TABLE} WHERE playerName=?"; //execute()
    private final HikariDataSource hikariDataSource;
    private final MySQLConfig mySQLConfig;

    public MySQLStorage(SimpleEconomyManager manager) {
        super(manager);
        val configFile = manager.getConfigFile();
        this.mySQLConfig = new MySQLConfig(configFile.getSQLSection(), configFile.getStorageType());
        this.hikariDataSource = new HikariDataSource();
        this.hikariDataSource.setJdbcUrl(this.mySQLConfig.getHost());
        this.hikariDataSource.setMaximumPoolSize(3);
        this.hikariDataSource.setPoolName("SimpleConomy-pool");
        this.hikariDataSource.setMaxLifetime(TimeUnit.MINUTES.toMillis(30L));
        this.hikariDataSource.setUsername(this.mySQLConfig.getUsername());
        this.hikariDataSource.setPassword(this.mySQLConfig.getPassword());

        try (val con = this.hikariDataSource.getConnection(); val statement = this.prepareStatement(con, CREATE_TABLE)) {
            statement.execute();
        } catch (SQLException e) {
            manager.getPlugin().getLogger().severe("Ошибка при создании таблицы MySQL " + e);
        }
    }

    private PreparedStatement prepareStatement(Connection connection, String sql) throws SQLException {
        return connection.prepareStatement(sql.replace("{TABLE}", this.mySQLConfig.getTable()));
    }

    private PlayerData loadPlayer(String nickName) {
        return CompletableFuture.supplyAsync(()-> {
            PlayerData  playerData = null;
            try (val con = this.hikariDataSource.getConnection(); val statement = this.prepareStatement(con, GET_PLAYER_DATA)) {
                statement.setString(1, nickName);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        val money = rs.getDouble("money");
                        val coins = rs.getInt("coins");
                        playerData = new PlayerData(nickName, money, coins);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Произошла ошибка при загрузке данных из MySQL " + nickName, e);
            }
            return playerData;
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        }).join();
    }

    @Override
    public void savePlayer(String nickName, PlayerData playerData) {
        CompletableFuture.runAsync(() -> {
            try (val con = this.hikariDataSource.getConnection(); val statement = this.prepareStatement(con, INSERT_PLAYER)) {
                statement.setString(1, nickName);
                statement.setString(2, String.valueOf((int) playerData.getMoney()));
                statement.setString(3, String.valueOf(playerData.getCoins()));
                statement.setString(4, nickName);
                statement.setString(5, String.valueOf((int) playerData.getMoney()));
                statement.setString(6, String.valueOf(playerData.getCoins()));
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Произошла ошибка при сохранении данных в MySQL " + nickName, e);
            }

        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Override
    public void cachePlayer(String nickName) {
        this.getPlayer(nickName);
    }

    @Override
    public void unCachePlayer(String nickName) {
        this.playersCache.remove(nickName);
    }

    @Override
    public boolean hasAccount(String nickName) {
        val temporalContainer = this.playersCache.get(nickName);
        if (temporalContainer != null) return true;
        PlayerData playerData = this.loadPlayer(nickName);
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
        val container = this.getPlayer(nickName);
        container.withdrawMoney(amount);
        this.savePlayer(nickName, container);
        return true;
    }

    @Override
    public boolean depositMoney(String nickName, double amount) {
        val container = this.getPlayer(nickName);
        container.depositMoney(amount);
        this.savePlayer(nickName, container);
        return true;
    }

    @Override
    public boolean setMoney(String nickName, double amount) {
        val container = this.getPlayer(nickName);
        container.setMoney(amount);
        this.savePlayer(nickName, container);
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
        val container = this.getPlayer(nickName);
        container.withdrawCoins(amount);
        this.savePlayer(nickName, container);
        return true;
    }

    @Override
    public boolean depositCoins(String nickName, int amount) {
        val container = this.getPlayer(nickName);
        container.depositCoins(amount);
        this.savePlayer(nickName, container);
        return true;
    }

    @Override
    public boolean setCoins(String nickName, int amount) {
        val container = this.getPlayer(nickName);
        container.setCoins(amount);
        this.savePlayer(nickName, container);
        return true;
    }

    @Override
    public boolean createAccount(String nickName) {
        System.out.println("S-ECON-DEBUG: MySQLStorage.class, createAccount method has been called.");
        return true;
    }

    @Override
    public PlayerData getPlayer(String nickName) {
        val container = this.playersCache.get(nickName);
        if (container != null) return container;
        PlayerData playerData = this.loadPlayer(nickName);
        val configFile = manager.getConfigFile();
        if (playerData == null) {
            playerData = new PlayerData(nickName, configFile.getStartBalance(), configFile.getStartCoins());
        }
        this.playersCache.put(nickName, playerData);
        return playerData;
    }

    @Override
    public void deleteAccount(String nickName) {
        CompletableFuture.runAsync(() -> {
            try (val con = this.hikariDataSource.getConnection(); val preparedStatement = this.prepareStatement(con, DELETE_PLAYER)) {
                preparedStatement.setString(1, nickName);
                preparedStatement.execute();
            } catch (SQLException ex) {
                throw new RuntimeException("Произошла ошибка при удалении данных из MySQL", ex);
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Override @SneakyThrows()
    public List<String> getMoneyTop(int amount) {
        List<String> list = new ArrayList<>(128);
        try (val con = this.hikariDataSource.getConnection(); val statement = this.prepareStatement(con, GET_MONEY_TOP.replace("{COUNT_IN_TOP}", String.valueOf(amount)))) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(this.manager.getConfigFile().formatTopLine(String.valueOf(list.size()+1), rs.getString("playerName"), String.valueOf(rs.getDouble("money"))));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Произошла ошибка при загрузке топа из MySQL ", e);
        }
        return list;
    }

    @Override @SneakyThrows()
    public List<String> getCoinsTop(int amount) {
        List<String> list = new ArrayList<>(128);
        try (val con = this.hikariDataSource.getConnection(); PreparedStatement statement = this.prepareStatement(con, GET_COINS_TOP.replace("{COUNT_IN_TOP}", String.valueOf(amount)))) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(this.manager.getConfigFile().formatTopLine(String.valueOf(list.size()+1), rs.getString("playerName"), String.valueOf(rs.getInt("coins"))));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Произошла ошибка при загрузке топа из MySQL ", e);
        }
        return list;
    }

    @Override
    public void close() {
        this.hikariDataSource.close();
    }
}
