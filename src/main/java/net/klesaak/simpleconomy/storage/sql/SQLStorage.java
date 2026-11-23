package net.klesaak.simpleconomy.storage.sql;

import lombok.Getter;
import net.klesaak.storagelib.pool.sql.SQLLoader;
import net.klesaak.simpleconomy.api.SimpleEconomyAPI;
import net.klesaak.simpleconomy.configurations.ConfigFile;
import net.klesaak.simpleconomy.manager.SimpleEconomyManager;
import net.klesaak.simpleconomy.storage.AbstractStorage;
import net.klesaak.simpleconomy.storage.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Getter
public class SQLStorage extends AbstractStorage implements SQLLoader {
    private final String createSimpleConomyTablesSql;

    private final String depositPlayerMoneySql;
    private final String depositPlayerCoinsSql;
    private final String withdrawPlayerMoneySql;
    private final String withdrawPlayerCoinsSql;
    private final String setPlayerMoneySql;
    private final String setPlayerCoinsSql;

    private final String fetchPlayerDataSql;
    private final String fetchPlayerMoneySql;
    private final String fetchPlayerCoinsSql;

    private final String deletePlayerDataSql;


    public SQLStorage(SimpleEconomyManager manager, ConfigFile configFile) {
        super(manager, configFile);
        this.createSimpleConomyTablesSql = this.loadSQL("createSimpleConomyTables",
                "%table%", configFile.getSqlTable());
        this.depositPlayerMoneySql = this.loadSQL("update/depositPlayerMoney",
                "%table%", configFile.getSqlTable(),
                "%maxBalance%", configFile.getMaxBalance());
        this.depositPlayerCoinsSql = this.loadSQL("update/depositPlayerCoins",
                "%table%", configFile.getSqlTable(),
                "%maxBalance%", configFile.getMaxBalance());
        this.withdrawPlayerMoneySql = this.loadSQL("update/withdrawPlayerMoney", "%table%", configFile.getSqlTable());
        this.withdrawPlayerCoinsSql = this.loadSQL("update/withdrawPlayerCoins", "%table%", configFile.getSqlTable());
        this.setPlayerMoneySql = this.loadSQL("update/setPlayerMoney",
                "%table%", configFile.getSqlTable(),
                "%maxBalance%", configFile.getMaxBalance());
        this.setPlayerCoinsSql = this.loadSQL("update/setPlayerCoins",
                "%table%", configFile.getSqlTable(),
                "%maxBalance%", configFile.getMaxCoins());
        this.fetchPlayerDataSql = this.loadSQL("fetch/fetchPlayerData", "%table%", configFile.getSqlTable());
        this.fetchPlayerMoneySql = this.loadSQL("fetch/fetchPlayerMoney", "%table%", configFile.getSqlTable());
        this.fetchPlayerCoinsSql = this.loadSQL("fetch/fetchPlayerCoins", "%table%", configFile.getSqlTable());
        this.deletePlayerDataSql = this.loadSQL("delete/deletePlayerData", "%table%", configFile.getSqlTable());
        this.executeSQL(configFile.getSQLPool().getHikariDataSource(), this.createSimpleConomyTablesSql);
    }

    @Override
    public CompletableFuture<PlayerData> loadPlayerAsync(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            PlayerData playerData = null;
            try (Connection con = ConfigFile.get().getSQLPool().getHikariDataSource().getConnection(); PreparedStatement statement = con.prepareStatement(this.fetchPlayerDataSql)) {
                statement.setString(1, playerName.toLowerCase(Locale.ROOT));
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        playerData = new PlayerData(resultSet.getInt("money_balance"), resultSet.getInt("coins_balance"));
                    }
                }
                return playerData;
            } catch (SQLException e) {
                throw new RuntimeException("Error while load player data from sql ", e);
            }
        }, ConfigFile.get().getSQLPool().getScheduler()).exceptionally(throwable -> {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, "Error while load player economy data", throwable);
            return null;
        });
    }

    @Override
    public void cache(String nickName) {
        String nickNameLC = nickName.toLowerCase(Locale.ROOT);
        PlayerData temporalPlayerData = this.temporalPlayersCache.getIfPresent(nickNameLC);
        if (temporalPlayerData != null) {
            this.playersCache.put(nickNameLC, temporalPlayerData);
            this.temporalPlayersCache.invalidate(nickNameLC);
            return;
        }
        this.loadPlayerAsync(nickNameLC).thenAccept(playerData -> {
            this.playersCache.put(nickNameLC, Objects.requireNonNullElseGet(playerData, () -> new PlayerData(0, 0)));
        });
    }

    @Override
    public void unCache(String nickName) {
        String nickNameLC = nickName.toLowerCase(Locale.ROOT);
        PlayerData playerData = this.playersCache.remove(nickNameLC);
        if (playerData != null) {
            this.temporalPlayersCache.put(nickNameLC, playerData);
        }
    }

    @Override
    public int getMoneyBalance(String nickName) {
        PlayerData playerData = this.getCachedPlayerData(nickName);
        if (playerData != null) return playerData.getMoney();
        return 0;
    }

    @Override
    public boolean hasMoney(String nickName, int amount) {
        return this.getMoneyBalance(nickName) >= amount;
    }

    @Override
    public boolean withdrawMoney(String nickName, int amount) {
        SimpleEconomyAPI.get().withdrawMoneyAsync(nickName, amount, null);
        return true;
    }

    @Override
    public boolean depositMoney(String nickName, int amount) {
        SimpleEconomyAPI.get().depositMoneyAsync(nickName, amount, null);
        return true;
    }

    @Override
    public int getCoinsBalance(String nickName) {
        PlayerData playerData = this.getCachedPlayerData(nickName);
        if (playerData != null) return playerData.getCoins();
        return 0;
    }

    @Override
    public void clearBalances(String nickName) {
        //todo
    }

    @Override
    public void close() {
        this.pubSubConnection.close();
        this.statefulRedisConnection.close();
    }
}