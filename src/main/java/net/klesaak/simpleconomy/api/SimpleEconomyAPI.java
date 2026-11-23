package net.klesaak.simpleconomy.api;

import net.klesaak.storagelib.pool.sql.SQLPool;
import net.klesaak.simpleconomy.configurations.ConfigFile;
import net.klesaak.simpleconomy.manager.SimpleEconomyManager;
import net.klesaak.simpleconomy.storage.PlayerData;
import net.klesaak.simpleconomy.storage.mesenger.MessageType;
import net.klesaak.simpleconomy.storage.sql.SQLStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class SimpleEconomyAPI {
    private static SimpleEconomyAPI INSTANCE;

    private final SimpleEconomyManager manager;

    private SimpleEconomyAPI(SimpleEconomyManager manager) {
        this.manager = manager;
    }

    public static void register(SimpleEconomyManager manager) {
        INSTANCE = new SimpleEconomyAPI(manager);
    }

    public static SimpleEconomyAPI get() {
        return INSTANCE;
    }

    public CompletableFuture<Integer> getMoneyBalanceAsync(String nickName) {
        ConfigFile configFile = ConfigFile.get();
        SQLPool sqlPool = configFile.getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
            PlayerData playerData = sqlStorage.getCachedPlayerData(nickName);
            if (playerData != null) return playerData.getMoney();
            try (Connection con = sqlPool.getHikariDataSource().getConnection(); PreparedStatement statement = con.prepareStatement(sqlStorage.getFetchPlayerMoneySql())) {
                statement.setString(1, nickName.toLowerCase(Locale.ROOT));
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("money_balance");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error while load player data from sql ", e);
            }
            return 0;
        }, sqlPool.getScheduler()).exceptionally(throwable -> {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while get money balance async.", throwable);
            return null;
        });
    }

    public CompletableFuture<Integer> getCoinsBalanceAsync(String nickName) {
        ConfigFile configFile = ConfigFile.get();
        SQLPool sqlPool = configFile.getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
            PlayerData playerData = sqlStorage.getCachedPlayerData(nickName);
            if (playerData != null) return playerData.getCoins();
            try (Connection con = sqlPool.getHikariDataSource().getConnection(); PreparedStatement statement = con.prepareStatement(sqlStorage.getFetchPlayerCoinsSql())) {
                statement.setString(1, nickName.toLowerCase(Locale.ROOT));
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("coins_balance");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error while load player data from sql ", e);
            }
            return 0;
        }, sqlPool.getScheduler()).exceptionally(throwable -> {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while get coins balance async.", throwable);
            return null;
        });
    }

    public CompletableFuture<Boolean> hasMoneyAsync(String nickName, int amount) {
        SQLPool sqlPool = ConfigFile.get().getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
            PlayerData playerData = sqlStorage.getCachedPlayerData(nickName);
            if (playerData != null) return playerData.getMoney() >= amount;
            try (Connection con = sqlPool.getHikariDataSource().getConnection(); PreparedStatement statement = con.prepareStatement(sqlStorage.getFetchPlayerMoneySql())) {
                statement.setString(1, nickName.toLowerCase(Locale.ROOT));
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("money_balance") >= amount;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error while load player data from sql ", e);
            }
            return false;
        }, sqlPool.getScheduler()).exceptionally(throwable -> {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while has balance async.", throwable);
            return false;
        });
    }

    public CompletableFuture<Boolean> hasCoinsAsync(String nickName, int amount) {
        SQLPool sqlPool = ConfigFile.get().getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
            PlayerData playerData = sqlStorage.getCachedPlayerData(nickName);
            if (playerData != null) return playerData.getCoins() >= amount;
            try (Connection con = sqlPool.getHikariDataSource().getConnection(); PreparedStatement statement = con.prepareStatement(sqlStorage.getFetchPlayerCoinsSql())) {
                statement.setString(1, nickName.toLowerCase(Locale.ROOT));
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("coins_balance") >= amount;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error while load player data from sql ", e);
            }
            return false;
        }, sqlPool.getScheduler()).exceptionally(throwable -> {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while has coins async.", throwable);
            return false;
        });
    }

    public CompletableFuture<Void> withdrawMoneyAsync(String nickName, int amount, String commentMiniMessage) {
        SQLPool sqlPool = ConfigFile.get().getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            try {
                SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
                PlayerData cachedPlayerData = sqlStorage.getCachedPlayerData(nickName);
                if (cachedPlayerData != null) {
                    if (cachedPlayerData.getMoney() - amount < 0) {
                        cachedPlayerData.setMoney(0);
                    } else {
                        cachedPlayerData.withdrawMoney(amount);
                    }
                }
                try (Connection con = sqlPool.getHikariDataSource().getConnection(); PreparedStatement statement = con.prepareStatement(sqlStorage.getWithdrawPlayerMoneySql())) {
                    statement.setString(1, nickName.toLowerCase(Locale.ROOT));
                    statement.setInt(2, amount);
                    statement.executeUpdate();
                }
                sqlStorage.publishPubSubUpdateMessage(nickName, MessageType.WITHDRAW_MONEY, amount, commentMiniMessage);
                return null;
            } catch (Throwable throwable) {
                this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while withdraw money async.", throwable);
            }
            return null;
        }, sqlPool.getScheduler());
    }

    public CompletableFuture<Void> depositMoneyAsync(String nickName, int amount, String commentMiniMessage) {
        ConfigFile configFile = ConfigFile.get();
        SQLPool sqlPool = configFile.getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            try {
                SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
                PlayerData cachedPlayerData = sqlStorage.getCachedPlayerData(nickName);
                if (cachedPlayerData != null) {
                    if (cachedPlayerData.getMoney() + amount > configFile.getMaxBalance()) {
                        cachedPlayerData.setMoney(configFile.getMaxBalance());
                    } else {
                        cachedPlayerData.depositMoney(amount);
                    }
                }
                try (Connection con = sqlPool.getHikariDataSource().getConnection(); PreparedStatement statement = con.prepareStatement(sqlStorage.getDepositPlayerMoneySql())) {
                    statement.setString(1, nickName.toLowerCase(Locale.ROOT));
                    statement.setInt(2, amount);
                    statement.executeUpdate();
                }
                sqlStorage.publishPubSubUpdateMessage(nickName, MessageType.DEPOSIT_MONEY, amount, commentMiniMessage);
            } catch (Throwable throwable) {
                this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while deposit money async.", throwable);
            }
            return null;
        }, sqlPool.getScheduler());
    }

    public CompletableFuture<Void> setMoneyAsync(String nickName, int amount, String commentMiniMessage) {
        ConfigFile configFile = ConfigFile.get();
        SQLPool sqlPool = configFile.getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            try {
                SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
                PlayerData cachedPlayerData = sqlStorage.getCachedPlayerData(nickName);
                if (cachedPlayerData != null) {
                    if (amount < 0) {
                        cachedPlayerData.setMoney(0);
                    } else {
                        cachedPlayerData.setMoney(Math.min(amount, configFile.getMaxBalance()));
                    }
                }
                try (Connection con = sqlPool.getHikariDataSource().getConnection(); PreparedStatement statement = con.prepareStatement(sqlStorage.getSetPlayerMoneySql())) {
                    statement.setString(1, nickName.toLowerCase(Locale.ROOT));
                    statement.setInt(2, amount);
                    statement.executeUpdate();
                }
                sqlStorage.publishPubSubUpdateMessage(nickName, MessageType.SET_MONEY, amount, commentMiniMessage);
            } catch (Throwable throwable) {
                this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while set money async.", throwable);
            }
            return null;
        }, sqlPool.getScheduler());
    }

    public CompletableFuture<Void> withdrawCoinsAsync(String nickName, int amount, String commentMiniMessage) {
        SQLPool sqlPool = ConfigFile.get().getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            try {
                SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
                PlayerData cachedPlayerData = sqlStorage.getCachedPlayerData(nickName);
                if (cachedPlayerData != null) {
                    if (cachedPlayerData.getCoins() - amount < 0) {
                        cachedPlayerData.setCoins(0);
                    } else {
                        cachedPlayerData.withdrawCoins(amount);
                    }
                }
                try (Connection con = sqlPool.getHikariDataSource().getConnection(); PreparedStatement statement = con.prepareStatement(sqlStorage.getWithdrawPlayerCoinsSql())) {
                    statement.setString(1, nickName.toLowerCase(Locale.ROOT));
                    statement.setInt(2, amount);
                    statement.executeUpdate();
                }
                sqlStorage.publishPubSubUpdateMessage(nickName, MessageType.WITHDRAW_COINS, amount, commentMiniMessage);
                return null;
            } catch (Throwable throwable) {
                this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while withdraw coins async.", throwable);
            }
            return null;
        }, sqlPool.getScheduler());
    }

    public CompletableFuture<Void> depositCoinsAsync(String nickName, int amount, String commentMiniMessage) {
        ConfigFile configFile = ConfigFile.get();
        SQLPool sqlPool = configFile.getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            try {
                SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
                PlayerData cachedPlayerData = sqlStorage.getCachedPlayerData(nickName);
                if (cachedPlayerData != null) {
                    if (cachedPlayerData.getCoins() + amount > configFile.getMaxCoins()) {
                        cachedPlayerData.setCoins(configFile.getMaxCoins());
                    } else {
                        cachedPlayerData.depositCoins(amount);
                    }
                }
                try (Connection con = sqlPool.getHikariDataSource().getConnection(); PreparedStatement statement = con.prepareStatement(sqlStorage.getDepositPlayerCoinsSql())) {
                    statement.setString(1, nickName.toLowerCase(Locale.ROOT));
                    statement.setInt(2, amount);
                    statement.executeUpdate();
                }
                sqlStorage.publishPubSubUpdateMessage(nickName, MessageType.DEPOSIT_COINS, amount, commentMiniMessage);
                return null;
            } catch (Throwable throwable) {
                this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while deposit coins async.", throwable);
            }
            return null;
        }, sqlPool.getScheduler());
    }

    public CompletableFuture<Void> setCoinsAsync(String nickName, int amount, String commentMiniMessage) {
        ConfigFile configFile = ConfigFile.get();
        SQLPool sqlPool = configFile.getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            try {
                SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
                PlayerData cachedPlayerData = sqlStorage.getCachedPlayerData(nickName);
                if (cachedPlayerData != null) {
                    if (amount < 0) {
                        cachedPlayerData.setCoins(0);
                    } else {
                        cachedPlayerData.setCoins(Math.min(amount, configFile.getMaxCoins()));
                    }
                }
                try (Connection con = sqlPool.getHikariDataSource().getConnection(); PreparedStatement statement = con.prepareStatement(sqlStorage.getSetPlayerCoinsSql())) {
                    statement.setString(1, nickName.toLowerCase(Locale.ROOT));
                    statement.setInt(2, amount);
                    statement.executeUpdate();
                }
                sqlStorage.publishPubSubUpdateMessage(nickName, MessageType.SET_COINS, amount, commentMiniMessage);
            } catch (Throwable throwable) {
                this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while set coins async.", throwable);
            }
            return null;
        }, sqlPool.getScheduler());
    }


    /**
     *
     * @param nickNamesAmountAndCommentMiniMessage - "nickName1", 228, "<red>Ваша команда проиграла на бедварс карта: Столбы и вы потеряли 50 денег."
     *                                               "nickName2", 1337, "<red>Ваша команда проиграла на бедварс карта: Столбы и вы потеряли 5099 денег."
     */
    public CompletableFuture<Void> withdrawCascadeMoneyAsync(Object... nickNamesAmountAndCommentMiniMessage) {
        ConfigFile configFile = ConfigFile.get();
        SQLPool sqlPool = configFile.getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (nickNamesAmountAndCommentMiniMessage.length == 0 || nickNamesAmountAndCommentMiniMessage.length % 3 != 0) {
                    throw new IllegalArgumentException("nicknames, sum and comment must be multiple of three");
                }
                SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
                try (Connection con = sqlPool.getHikariDataSource().getConnection()) {
                    con.setAutoCommit(false);
                    try (PreparedStatement statement = con.prepareStatement(sqlStorage.getWithdrawPlayerMoneySql())) {
                        int i = 0;
                        for (Object placeholder : nickNamesAmountAndCommentMiniMessage) {
                            if (i % 3 == 0) {
                                String nickName = String.valueOf(placeholder).toLowerCase(Locale.ROOT);
                                int amount = Integer.parseInt(String.valueOf(nickNamesAmountAndCommentMiniMessage[i + 1]));
                                String comment = String.valueOf(nickNamesAmountAndCommentMiniMessage[i + 2]);
                                statement.setString(1, nickName);
                                statement.setInt(2, amount);
                                statement.addBatch();
                                PlayerData cachedPlayerData = sqlStorage.getCachedPlayerData(nickName);
                                if (cachedPlayerData != null) {
                                    if (cachedPlayerData.getMoney() - amount < 0) {
                                        cachedPlayerData.setMoney(0);
                                    } else {
                                        cachedPlayerData.withdrawMoney(amount);
                                    }
                                }
                                sqlStorage.publishPubSubUpdateMessage(nickName, MessageType.WITHDRAW_MONEY, amount, comment);
                            }
                            ++i;
                        }
                    } catch (SQLException e) {
                        con.rollback();
                        con.setAutoCommit(true);
                        throw e;
                    }
                    con.commit();
                    con.setAutoCommit(true);
                }
            } catch (Throwable throwable) {
                this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while withdraw cascade money async.", throwable);
            }
            return null;
        }, sqlPool.getScheduler());
    }

    /**
     *
     * @param nickNamesAmountAndCommentMiniMessage - "nickName1", 228, "<red>Ваша команда проиграла на бедварс карта: Столбы и вы потеряли 50 коинс."
     *                                               "nickName2", 1337, "<red>Ваша команда проиграла на бедварс карта: Столбы и вы потеряли 5099 коинс."
     */
    public CompletableFuture<Void> withdrawCascadeCoinsAsync(Object... nickNamesAmountAndCommentMiniMessage) {
        ConfigFile configFile = ConfigFile.get();
        SQLPool sqlPool = configFile.getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (nickNamesAmountAndCommentMiniMessage.length == 0 || nickNamesAmountAndCommentMiniMessage.length % 3 != 0) {
                    throw new IllegalArgumentException("nicknames, sum and comment must be multiple of three");
                }
                SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
                try (Connection con = sqlPool.getHikariDataSource().getConnection()) {
                    con.setAutoCommit(false);
                    try (PreparedStatement statement = con.prepareStatement(sqlStorage.getWithdrawPlayerCoinsSql())) {
                        int i = 0;
                        for (Object placeholder : nickNamesAmountAndCommentMiniMessage) {
                            if (i % 3 == 0) {
                                String nickName = String.valueOf(placeholder).toLowerCase(Locale.ROOT);
                                int amount = Integer.parseInt(String.valueOf(nickNamesAmountAndCommentMiniMessage[i + 1]));
                                String comment = String.valueOf(nickNamesAmountAndCommentMiniMessage[i + 2]);
                                statement.setString(1, nickName);
                                statement.setInt(2, amount);
                                statement.addBatch();
                                PlayerData cachedPlayerData = sqlStorage.getCachedPlayerData(nickName);
                                if (cachedPlayerData != null) {
                                    if (cachedPlayerData.getCoins() - amount < 0) {
                                        cachedPlayerData.setCoins(0);
                                    } else {
                                        cachedPlayerData.withdrawCoins(amount);
                                    }
                                }
                                sqlStorage.publishPubSubUpdateMessage(nickName, MessageType.WITHDRAW_COINS, amount, comment);
                            }
                            ++i;
                        }
                    } catch (SQLException e) {
                        con.rollback();
                        con.setAutoCommit(true);
                        throw e;
                    }
                    con.commit();
                    con.setAutoCommit(true);
                }
            } catch (Throwable throwable) {
                this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while withdraw cascade coins async.", throwable);
            }
            return null;
        }, sqlPool.getScheduler());
    }

    /**
     *
     * @param nickNamesAmountAndCommentMiniMessage - "nickName1", 228, "<red>Ваша команда выиграла на бедварс карта: Столбы и вы получили 50 денег."
     *                                               "nickName2", 1337, "<red>Ваша команда выиграла на бедварс карта: Столбы и вы потеряли 5099 денег."
     */
    public CompletableFuture<Void> depositCascadeMoneyAsync(Object... nickNamesAmountAndCommentMiniMessage) {
        ConfigFile configFile = ConfigFile.get();
        SQLPool sqlPool = configFile.getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (nickNamesAmountAndCommentMiniMessage.length == 0 || nickNamesAmountAndCommentMiniMessage.length % 3 != 0) {
                    throw new IllegalArgumentException("nicknames, sum and comment must be multiple of three");
                }
                SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
                try (Connection con = sqlPool.getHikariDataSource().getConnection()) {
                    con.setAutoCommit(false);
                    try (PreparedStatement statement = con.prepareStatement(sqlStorage.getDepositPlayerMoneySql())) {
                        int i = 0;
                        for (Object placeholder : nickNamesAmountAndCommentMiniMessage) {
                            if (i % 3 == 0) {
                                String nickName = String.valueOf(placeholder).toLowerCase(Locale.ROOT);
                                int amount = Integer.parseInt(String.valueOf(nickNamesAmountAndCommentMiniMessage[i + 1]));
                                String comment = String.valueOf(nickNamesAmountAndCommentMiniMessage[i + 2]);
                                statement.setString(1, nickName);
                                statement.setInt(2, amount);
                                statement.addBatch();
                                PlayerData cachedPlayerData = sqlStorage.getCachedPlayerData(nickName);
                                if (cachedPlayerData != null) {
                                    if (cachedPlayerData.getMoney() + amount > configFile.getMaxBalance()) {
                                        cachedPlayerData.setMoney(configFile.getMaxBalance());
                                    } else {
                                        cachedPlayerData.depositMoney(amount);
                                    }
                                }
                                sqlStorage.publishPubSubUpdateMessage(nickName, MessageType.DEPOSIT_MONEY, amount, comment);
                            }
                            ++i;
                        }
                    } catch (SQLException e) {
                        con.rollback();
                        con.setAutoCommit(true);
                        throw e;
                    }
                    con.commit();
                    con.setAutoCommit(true);
                }
            } catch (Throwable throwable) {
                this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while deposit cascade money async.", throwable);
            }
            return null;
        }, sqlPool.getScheduler());
    }

    /**
     *
     * @param nickNamesAmountAndCommentMiniMessage - "nickName1", 228, "<red>Ваша команда выиграла на бедварс карта: Столбы и вы получили 50 коинс."
     *                                               "nickName2", 1337, "<red>Ваша команда выиграла на бедварс карта: Столбы и вы потеряли 5099 коинс."
     */
    public CompletableFuture<Void> depositCascadeCoinsAsync(Object... nickNamesAmountAndCommentMiniMessage) {
        ConfigFile configFile = ConfigFile.get();
        SQLPool sqlPool = configFile.getSQLPool();
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (nickNamesAmountAndCommentMiniMessage.length == 0 || nickNamesAmountAndCommentMiniMessage.length % 3 != 0) {
                    throw new IllegalArgumentException("nicknames, sum and comment must be multiple of three");
                }
                SQLStorage sqlStorage = (SQLStorage) this.manager.getStorage();
                try (Connection con = sqlPool.getHikariDataSource().getConnection()) {
                    con.setAutoCommit(false);
                    try (PreparedStatement statement = con.prepareStatement(sqlStorage.getDepositPlayerCoinsSql())) {
                        int i = 0;
                        for (Object placeholder : nickNamesAmountAndCommentMiniMessage) {
                            if (i % 3 == 0) {
                                String nickName = String.valueOf(placeholder).toLowerCase(Locale.ROOT);
                                int amount = Integer.parseInt(String.valueOf(nickNamesAmountAndCommentMiniMessage[i + 1]));
                                String comment = String.valueOf(nickNamesAmountAndCommentMiniMessage[i + 2]);
                                statement.setString(1, nickName);
                                statement.setInt(2, amount);
                                statement.addBatch();
                                PlayerData cachedPlayerData = sqlStorage.getCachedPlayerData(nickName);
                                if (cachedPlayerData != null) {
                                    if (cachedPlayerData.getCoins() + amount > configFile.getMaxCoins()) {
                                        cachedPlayerData.setCoins(configFile.getMaxCoins());
                                    } else {
                                        cachedPlayerData.depositCoins(amount);
                                    }
                                }
                                sqlStorage.publishPubSubUpdateMessage(nickName, MessageType.DEPOSIT_MONEY, amount, comment);
                            }
                            ++i;
                        }
                    } catch (SQLException e) {
                        con.rollback();
                        con.setAutoCommit(true);
                        throw e;
                    }
                    con.commit();
                    con.setAutoCommit(true);
                }
            } catch (Throwable throwable) {
                this.manager.getPlugin().getLogger().log(Level.SEVERE, "SimpleEconomyAPI got error while deposit cascade coins async.", throwable);
            }
            return null;
        }, sqlPool.getScheduler());
    }
}