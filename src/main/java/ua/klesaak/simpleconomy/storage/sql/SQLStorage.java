package ua.klesaak.simpleconomy.storage.sql;

import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.val;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.storage.AbstractStorage;
import ua.klesaak.simpleconomy.storage.PlayerData;
import ua.klesaak.simpleconomy.storage.sql.driver.AbstractConnectionFactory;
import ua.klesaak.simpleconomy.storage.sql.driver.MariaDbConnectionFactory;
import ua.klesaak.simpleconomy.storage.sql.driver.MySqlConnectionFactory;
import ua.klesaak.simpleconomy.storage.sql.driver.PostgresConnectionFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SQLStorage extends AbstractStorage implements SQLLoader {
    public final String createTableSql;
    public final String insertPlayerSql;
    public final String fetchPlayerSql;
    public final String getMoneyTopSql;
    public final String getCoinsTopSql;
    public final String deletePlayerSql;
    private final HikariDataSource hikariDataSource;

    public SQLStorage(SimpleEconomyManager manager) {
        super(manager);
        val configFile = manager.getConfigFile();
        val sqlConfig = new SQLConfig(configFile.getSQLSection());
        this.createTableSql = this.loadSQL("createTables", "%tableName%", sqlConfig.getTable());
        this.fetchPlayerSql = this.loadSQL("fetch/fetchPlayer", "%tableName%", sqlConfig.getTable());
        this.getMoneyTopSql = this.loadSQL("fetch/getMoneyTop", "%tableName%", sqlConfig.getTable(), "%countInTop%", String.valueOf(configFile.getPlayerTopMoneyCount()));
        this.getCoinsTopSql = this.loadSQL("fetch/getCoinsTop", "%tableName%", sqlConfig.getTable(), "%countInTop%", String.valueOf(configFile.getPlayerTopCoinsCount()));
        this.deletePlayerSql = this.loadSQL("update/deletePlayer", "%tableName%", sqlConfig.getTable());
        this.insertPlayerSql = this.loadSQL("update/insertPlayer", "%tableName%", sqlConfig.getTable());
        AbstractConnectionFactory connectionFactory = new MySqlConnectionFactory(null, null, null, null, null, false);
        switch (configFile.getStorageType()) {
            case MARIADB: {
                connectionFactory = new MariaDbConnectionFactory(sqlConfig.getUsername(), sqlConfig.getPassword(),
                        sqlConfig.getAddress(), sqlConfig.getPort(),
                        sqlConfig.getDatabase(), sqlConfig.isUseSSL());
                break;
            }
            case MYSQL: {
                connectionFactory = new MySqlConnectionFactory(sqlConfig.getUsername(), sqlConfig.getPassword(),
                        sqlConfig.getAddress(), sqlConfig.getPort(),
                        sqlConfig.getDatabase(), sqlConfig.isUseSSL());
                break;
            }
            case POSTGRESQL: {
                connectionFactory = new PostgresConnectionFactory(sqlConfig.getUsername(), sqlConfig.getPassword(),
                        sqlConfig.getAddress(), sqlConfig.getPort(),
                        sqlConfig.getDatabase(), sqlConfig.isUseSSL());
                break;
            }
        }

        this.hikariDataSource = new HikariDataSource(connectionFactory.getHikariConfig());
        this.executeSQL(this.hikariDataSource, this.createTableSql);
    }

    private PlayerData loadPlayer(String nickName) {
        return CompletableFuture.supplyAsync(()-> {
            PlayerData  playerData = null;
            try (val con = this.hikariDataSource.getConnection(); val statement = con.prepareStatement(this.fetchPlayerSql)) {
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
            this.manager.getPlugin().getLogger().log(Level.SEVERE, throwable.getMessage());
            return null;
        }).join();
    }

    @Override
    public void savePlayer(String nickName, PlayerData playerData) {
        CompletableFuture.runAsync(() -> {
            try (val con = this.hikariDataSource.getConnection(); val statement = con.prepareStatement(this.insertPlayerSql)) {
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
            this.manager.getPlugin().getLogger().log(Level.SEVERE, throwable.getMessage());
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
            try (val con = this.hikariDataSource.getConnection(); val preparedStatement = con.prepareStatement(this.deletePlayerSql)) {
                preparedStatement.setString(1, nickName);
                preparedStatement.execute();
            } catch (SQLException ex) {
                throw new RuntimeException("Произошла ошибка при удалении данных из MySQL", ex);
            }
        }).exceptionally(throwable -> {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, throwable.getMessage());
            return null;
        });
    }

    @Override @SneakyThrows()
    public List<String> getMoneyTop(int amount) {
        List<String> list = new ArrayList<>(128);
        try (val con = this.hikariDataSource.getConnection(); val statement = con.prepareStatement(this.getMoneyTopSql)) {
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
        try (val con = this.hikariDataSource.getConnection(); PreparedStatement statement = con.prepareStatement(this.getCoinsTopSql)) {
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
