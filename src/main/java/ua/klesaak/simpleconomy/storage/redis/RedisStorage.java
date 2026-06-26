package ua.klesaak.simpleconomy.storage.redis;

import io.lettuce.core.api.sync.RedisCommands;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.manager.TopManager;
import ua.klesaak.simpleconomy.storage.AbstractStorage;
import ua.klesaak.simpleconomy.storage.PlayerData;
import ua.klesaak.simpleconomy.storage.redis.messenger.MessageData;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class RedisStorage extends AbstractStorage {
    private final RedisConfig redisConfig;
    private final RedisMessenger redisMessenger;

    private final RedisCommands<String, String> redisCommands;

    public RedisStorage(SimpleEconomyManager manager) {
        RedisConfig config = new RedisConfig(manager.getConfigFile().getRedisSection());
        this.redisConfig = config;
        super(manager, config.getThreads());

        this.redisMessenger = new RedisMessenger(manager, this.redisConfig);
        this.redisCommands = this.redisConfig.getStatefulRedisConnection().sync();

        manager.getPlugin().getLogger().info("RedisStorage has been started!");
    }

    @Override
    public void cache(String nickName) {
        PlayerData playerData = new PlayerData(0.0, 0);
        String playerNameLC = nickName.toLowerCase(Locale.ROOT);
        CompletableFuture.runAsync(() -> {
            String money = this.redisCommands.hget(this.redisConfig.getBalanceKey(), playerNameLC);
            playerData.setMoney(money == null ? manager.getConfigFile().getStartBalance() : Double.parseDouble(money));
            String coins = this.redisCommands.hget(this.redisConfig.getCoinsKey(), playerNameLC);
            playerData.setCoins(coins == null ? manager.getConfigFile().getStartCoins() : Integer.parseInt(coins));
        }, this.executorService).exceptionally(throwable -> {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, throwable.getMessage());
            return null;
        });
        this.playersCache.put(playerNameLC, playerData);
    }

    @Override
    public void unCache(String nickName) {
        this.playersCache.remove(nickName.toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean hasAccount(String nickName) {
        return true;
    }

    @Override
    public double getMoneyBalance(String nickName) {
        String playerNameLC = nickName.toLowerCase(Locale.ROOT);
        PlayerData playerData = this.playersCache.get(playerNameLC);
        if (playerData != null) {
            return playerData.getMoney();
        }
        try {
            String money = this.redisCommands.hget(this.redisConfig.getBalanceKey(), playerNameLC);
            return money == null ? manager.getConfigFile().getStartBalance() : Double.parseDouble(money);
        } catch (Exception e) {
            manager.getPlugin().getLogger().warning(e.getMessage());
        }
        return 0.0;
    }

    @Override
    public boolean hasMoney(String nickName, double amount) {
        return this.getMoneyBalance(nickName.toLowerCase(Locale.ROOT)) >= amount;
    }

    @Override
    public boolean withdrawMoney(String nickName, double amount) {
        String playerNameLC = nickName.toLowerCase(Locale.ROOT);
        double result = this.getMoneyBalance(playerNameLC) - amount;
        if (result < 0) result = 0;
        this.setMoney(playerNameLC, result);
        return true;
    }

    @Override
    public boolean depositMoney(String nickName, double amount) {
        String playerNameLC = nickName.toLowerCase(Locale.ROOT);
        double result = this.getMoneyBalance(playerNameLC) + amount;
        this.setMoney(playerNameLC, result);
        return true;
    }

    @Override
    public boolean setMoney(String nickName, double amount) {
        String playerNameLC = nickName.toLowerCase(Locale.ROOT);
        PlayerData playerData = this.playersCache.get(playerNameLC);
        if (playerData != null) {
            playerData.setMoney(amount);
        }
        CompletableFuture.runAsync(() -> {
            this.redisCommands.hset(this.redisConfig.getBalanceKey(), playerNameLC, String.valueOf(amount));
            this.redisMessenger.publishMoneyMessage(new MessageData(playerNameLC, amount));
        }, this.executorService).exceptionally(throwable -> {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, throwable.getMessage());
            return null;
        });
        return true;
    }

    @Override
    public int getCoinsBalance(String nickName) {
        String playerNameLC = nickName.toLowerCase(Locale.ROOT);
        PlayerData playerData = this.playersCache.get(playerNameLC);
        if (playerData != null) {
            return playerData.getCoins();
        }
        try {
            String coins = this.redisCommands.hget(this.redisConfig.getCoinsKey(), playerNameLC);
            return coins == null ? manager.getConfigFile().getStartCoins() : Integer.parseInt(coins);
        } catch (Exception e) {
            manager.getPlugin().getLogger().warning(e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean hasCoins(String nickName, int amount) {
        return this.getCoinsBalance(nickName.toLowerCase(Locale.ROOT)) >= amount;
    }

    @Override
    public boolean withdrawCoins(String nickName, int amount) {
        String playerNameLC = nickName.toLowerCase(Locale.ROOT);
        int result = this.getCoinsBalance(playerNameLC) - amount;
        if (result < 0) result = 0;
        this.setCoins(playerNameLC, result);
        return true;
    }

    @Override
    public boolean depositCoins(String nickName, int amount) {
        String playerNameLC = nickName.toLowerCase(Locale.ROOT);
        int result = this.getCoinsBalance(playerNameLC) + amount;
        this.setCoins(playerNameLC, result);
        return true;
    }

    @Override
    public boolean setCoins(String nickName, int amount) {
        String playerNameLC = nickName.toLowerCase(Locale.ROOT);
        PlayerData playerData = this.playersCache.get(playerNameLC);
        if (playerData != null) {
            playerData.setCoins(amount);
        }
        CompletableFuture.runAsync(() -> {
            this.redisCommands.hset(this.redisConfig.getCoinsKey(), playerNameLC, String.valueOf(amount));
            this.redisMessenger.publishCoinsMessage(new MessageData(playerNameLC, amount));
        }, this.executorService).exceptionally(throwable -> {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, throwable.getMessage());
            return null;
        });
        return true;
    }

    @Override
    public boolean createAccount(String nickName) {
        System.out.println("S-ECON-DEBUG: RedisStorage.class, createAccount method has been called.");
        return true;
    }

    @Override
    public void clearBalances(String nickName) {
        String playerNameLC = nickName.toLowerCase(Locale.ROOT);
        CompletableFuture.runAsync(() -> {
            this.redisCommands.hdel(this.redisConfig.getBalanceKey(), playerNameLC);
            this.redisCommands.hdel(this.redisConfig.getCoinsKey(), playerNameLC);
        }).exceptionally(throwable -> {
            manager.getPlugin().getLogger().info(throwable.getMessage());
            return null;
        });
    }

    @Override
    public List<TopManager.TopLineDouble> getMoneyTop(int amount) {
        Object2DoubleMap<String> doubleMap = new Object2DoubleOpenHashMap<>();
        this.redisCommands.hgetall(this.redisConfig.getBalanceKey()).forEach((s, s2) -> doubleMap.put(s, Double.parseDouble(s2)));
        List<String> keys = new ArrayList<>(doubleMap.keySet());
        keys.sort(Comparator.comparingDouble(doubleMap::getDouble));
        var dataList = new ArrayList<TopManager.TopLineDouble>();
        for (int i = 0; i < amount && keys.size() != i; i++) {
            String key = keys.get(i);
            dataList.add(new TopManager.TopLineDouble(key, doubleMap.getDouble(key), i+1));
        }
        return dataList;
    }

    @Override
    public List<TopManager.TopLineInteger> getCoinsTop(int amount) {
        Object2IntMap<String> intMap = new Object2IntOpenHashMap<>();
        this.redisCommands.hgetall(this.redisConfig.getCoinsKey()).forEach((s, s2) -> intMap.put(s, Integer.parseInt(s2)));
        List<String> keys = new ArrayList<>(intMap.keySet());
        keys.sort(Comparator.comparingInt(intMap::getInt));
        var dataList = new ArrayList<TopManager.TopLineInteger>();
        for (int i = 0; i < amount && keys.size() != i; i++) {
            String key = keys.get(i);
            dataList.add(new TopManager.TopLineInteger(key, intMap.getInt(key), i+1));
        }
        return dataList;
    }

    @Override
    public void close() {
        if (this.redisConfig != null) this.redisConfig.close();
        if (this.redisMessenger != null) this.redisMessenger.close();
        this.executorService.shutdown();
    }
}
