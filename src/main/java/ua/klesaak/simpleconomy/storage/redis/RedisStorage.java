package ua.klesaak.simpleconomy.storage.redis;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import redis.clients.jedis.RedisClient;
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

    public RedisStorage(SimpleEconomyManager manager) {
        super(manager);
        this.redisConfig = new RedisConfig(manager.getConfigFile().getRedisSection());
        this.redisMessenger = new RedisMessenger(manager, this.redisConfig);
        manager.getPlugin().getLogger().info("RedisStorage has been started!");
    }

    @Override
    public void cache(String nickName) {
        PlayerData playerData = new PlayerData(0.0, 0);
        CompletableFuture.runAsync(() -> {
            String money = this.redisConfig.getRedisClient().hget(this.redisConfig.getBalanceKey(), nickName);
            playerData.setMoney(money == null ? manager.getConfigFile().getStartBalance() : Double.parseDouble(money));
            String coins = this.redisConfig.getRedisClient().hget(this.redisConfig.getCoinsKey(), nickName);
            playerData.setCoins(coins == null ? manager.getConfigFile().getStartCoins() : Integer.parseInt(coins));
        }, this.executorService).exceptionally(throwable -> {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, throwable.getMessage());
            return null;
        });
        this.playersCache.put(nickName, playerData);
    }

    @Override
    public void unCache(String nickName) {
        this.playersCache.remove(nickName);
    }

    @Override
    public boolean hasAccount(String nickName) {
        return true;
    }

    @Override
    public double getMoneyBalance(String nickName) {
        PlayerData playerData = this.playersCache.get(nickName);
        if (playerData != null) {
            return playerData.getMoney();
        }
        try {
            String money = this.redisConfig.getRedisClient().hget(this.redisConfig.getBalanceKey(), nickName);
            return money == null ? manager.getConfigFile().getStartBalance() : Double.parseDouble(money);
        } catch (Exception e) {
            manager.getPlugin().getLogger().warning(e.getMessage());
        }
        return 0.0;
    }

    @Override
    public boolean hasMoney(String nickName, double amount) {
        return this.getMoneyBalance(nickName) >= amount;
    }

    @Override
    public boolean withdrawMoney(String nickName, double amount) {
        double result = this.getMoneyBalance(nickName) - amount;
        if (result < 0) result = 0;
        this.setMoney(nickName, result);
        return true;
    }

    @Override
    public boolean depositMoney(String nickName, double amount) {
        double result = this.getMoneyBalance(nickName) + amount;
        this.setMoney(nickName, result);
        return true;
    }

    @Override
    public boolean setMoney(String nickName, double amount) {
        PlayerData playerData = this.playersCache.get(nickName);
        if (playerData != null) {
            playerData.setMoney(amount);
        }
        CompletableFuture.runAsync(() -> {
            this.redisConfig.getRedisClient().hset(this.redisConfig.getBalanceKey(), nickName, String.valueOf(amount));
            this.redisMessenger.publishMoneyMessage(new MessageData(nickName, amount));
        }, this.executorService).exceptionally(throwable -> {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, throwable.getMessage());
            return null;
        });
        return true;
    }

    @Override
    public int getCoinsBalance(String nickName) {
        PlayerData playerData = this.playersCache.get(nickName);
        if (playerData != null) {
            return playerData.getCoins();
        }
        try {
            String coins = this.redisConfig.getRedisClient().hget(this.redisConfig.getCoinsKey(), nickName);
            return coins == null ? manager.getConfigFile().getStartCoins() : Integer.parseInt(coins);
        } catch (Exception e) {
            manager.getPlugin().getLogger().warning(e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean hasCoins(String nickName, int amount) {
        return this.getCoinsBalance(nickName) >= amount;
    }

    @Override
    public boolean withdrawCoins(String nickName, int amount) {
        int result = this.getCoinsBalance(nickName) - amount;
        if (result < 0) result = 0;
        this.setCoins(nickName, result);
        return true;
    }

    @Override
    public boolean depositCoins(String nickName, int amount) {
        int result = this.getCoinsBalance(nickName) + amount;
        this.setCoins(nickName, result);
        return true;
    }

    @Override
    public boolean setCoins(String nickName, int amount) {
        PlayerData playerData = this.playersCache.get(nickName);
        if (playerData != null) {
            playerData.setCoins(amount);
        }
        CompletableFuture.runAsync(() -> {
            this.redisConfig.getRedisClient().hset(this.redisConfig.getCoinsKey(), nickName, String.valueOf(amount));
            this.redisMessenger.publishCoinsMessage(new MessageData(nickName, amount));
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
        CompletableFuture.runAsync(() -> {
            RedisClient redisClient = this.redisConfig.getRedisClient();
            redisClient.hdel(this.redisConfig.getBalanceKey(), nickName);
            redisClient.hdel(this.redisConfig.getCoinsKey(), nickName);
        }).exceptionally(throwable -> {
            manager.getPlugin().getLogger().info(throwable.getMessage());
            return null;
        });
    }

    @Override
    public List<TopManager.TopLineDouble> getMoneyTop(int amount) {
        TObjectDoubleMap<String> doubleMap = new TObjectDoubleHashMap<>();
        this.redisConfig.getRedisClient().hgetAll(this.redisConfig.getBalanceKey()).forEach((s, s2) -> doubleMap.put(s, Double.parseDouble(s2)));
        List<String> keys = new ArrayList<>(doubleMap.keySet());
        keys.sort(Comparator.comparingDouble(doubleMap::get));
        var dataList = new ArrayList<TopManager.TopLineDouble>();
        for (int i = 0; i < amount && keys.size() != i; i++) {
            String key = keys.get(i);
            dataList.add(new TopManager.TopLineDouble(key, doubleMap.get(key), i+1));
        }
        return dataList;
    }

    @Override
    public List<TopManager.TopLineInteger> getCoinsTop(int amount) {
        TObjectIntMap<String> intMap = new TObjectIntHashMap<>();
        this.redisConfig.getRedisClient().hgetAll(this.redisConfig.getCoinsKey()).forEach((s, s2) -> intMap.put(s, Integer.parseInt(s2)));
        List<String> keys = new ArrayList<>(intMap.keySet());
        keys.sort(Comparator.comparingInt(intMap::get));
        var dataList = new ArrayList<TopManager.TopLineInteger>();
        for (int i = 0; i < amount && keys.size() != i; i++) {
            String key = keys.get(i);
            dataList.add(new TopManager.TopLineInteger(key, intMap.get(key), i+1));
        }
        return dataList;
    }

    @Override
    public void close() {
        if (this.redisConfig != null) this.redisConfig.close();
        if (this.redisMessenger != null) this.redisMessenger.close();
    }
}
