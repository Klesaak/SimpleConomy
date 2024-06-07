package ua.klesaak.simpleconomy.storage.redis;

import lombok.val;
import redis.clients.jedis.Jedis;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.manager.TopManager;
import ua.klesaak.simpleconomy.storage.AbstractStorage;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RedisStorage extends AbstractStorage {
    private final RedisConfig redisConfig;
    private final RedisConfig.RedisPool redisPool;

    public RedisStorage(SimpleEconomyManager manager) {
        super(manager);
        this.redisConfig = new RedisConfig(manager.getConfigFile().getRedisSection());
        this.redisPool = this.redisConfig.newRedisPool();
        manager.getPlugin().getLogger().info("RedisStorage has been started!");
    }

    @Override
    public boolean hasAccount(String nickName) {
        return true;
    }

    @Override
    public double getMoneyBalance(String nickName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = this.redisPool.getRedis()) {
                jedis.select(this.redisConfig.getDatabase());
                String money = jedis.hget(this.redisConfig.getBalanceKey(), nickName);
                return money == null ? manager.getConfigFile().getStartBalance() : Double.parseDouble(money);
            }
        }).exceptionally(throwable -> {
            manager.getPlugin().getLogger().info(throwable.getMessage());
            return 0.0;
        }).join();
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
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = this.redisPool.getRedis()) {
                jedis.select(this.redisConfig.getDatabase());
                jedis.hset(this.redisConfig.getBalanceKey(), nickName, String.valueOf(amount));
                return true;
            }
        }).exceptionally(throwable -> {
            manager.getPlugin().getLogger().info(throwable.getMessage());
            return false;
        }).join();
    }

    @Override
    public int getCoinsBalance(String nickName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = this.redisPool.getRedis()) {
                jedis.select(this.redisConfig.getDatabase());
                String coins = jedis.hget(this.redisConfig.getCoinsKey(), nickName);
                return coins == null ? manager.getConfigFile().getStartCoins() : Integer.parseInt(coins);
            }
        }).exceptionally(throwable -> {
            manager.getPlugin().getLogger().info(throwable.getMessage());
            return 0;
        }).join();
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
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = this.redisPool.getRedis()) {
                jedis.select(this.redisConfig.getDatabase());
                jedis.hset(this.redisConfig.getCoinsKey(), nickName, String.valueOf(amount));
                return true;
            }
        }).exceptionally(throwable -> {
            manager.getPlugin().getLogger().info(throwable.getMessage());
            return false;
        }).join();
    }

    @Override
    public boolean createAccount(String nickName) {
        System.out.println("S-ECON-DEBUG: JsonStorage.class, createAccount method has been called.");
        return true;
    }

    @Override
    public void clearBalances(String nickName) {
        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = this.redisPool.getRedis()) {
                jedis.select(this.redisConfig.getDatabase());
                jedis.hdel(this.redisConfig.getBalanceKey(), nickName);
                jedis.hdel(this.redisConfig.getCoinsKey(), nickName);
            }
        }).exceptionally(throwable -> {
            manager.getPlugin().getLogger().info(throwable.getMessage());
            return null;
        });
    }

    @Override
    public List<TopManager.TopLineDouble> getMoneyTop(int amount) {
        Map<String, Double> map = new HashMap<>();
        try (Jedis jedis = this.redisPool.getRedis()) {
            jedis.select(this.redisConfig.getDatabase());
            jedis.hgetAll(this.redisConfig.getBalanceKey()).forEach((s, s2) -> map.put(s, Double.parseDouble(s2)));
        }
        val dataList = new ArrayList<TopManager.TopLineDouble>();
        val sortedList = map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        Collections.reverse(sortedList);
        int sortedListSize = sortedList.size();
        for (int i = 0; i < amount && sortedListSize != i; i++) {
            val entry = sortedList.get(i);
            dataList.add(new TopManager.TopLineDouble(entry.getKey(), entry.getValue(), i+1));
        }
        return dataList;
    }

    @Override
    public List<TopManager.TopLineInteger> getCoinsTop(int amount) {
        Map<String, Integer> map = new HashMap<>();
        try (Jedis jedis = this.redisPool.getRedis()) {
            jedis.select(this.redisConfig.getDatabase());
            jedis.hgetAll(this.redisConfig.getCoinsKey()).forEach((s, s2) -> map.put(s, Integer.parseInt(s2)));
        }
        val dataList = new ArrayList<TopManager.TopLineInteger>();
        val sortedList = map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        Collections.reverse(sortedList);
        int sortedListSize = sortedList.size();
        for (int i = 0; i < amount && sortedListSize != i; i++) {
            val entry = sortedList.get(i);
            dataList.add(new TopManager.TopLineInteger(entry.getKey(), entry.getValue(), i+1));
        }
        return dataList;
    }

    @Override
    public void close() {
        if (this.redisPool != null) this.redisPool.close();
    }
}
