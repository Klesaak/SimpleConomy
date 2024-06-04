package ua.klesaak.simpleconomy.storage.redis;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import lombok.val;
import redis.clients.jedis.Jedis;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.manager.TopManager;
import ua.klesaak.simpleconomy.storage.AbstractStorage;

import java.util.*;
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
        try (Jedis jedis = this.redisPool.getRedis()) {
            jedis.select(this.redisConfig.getDatabase());
            String money = jedis.hget(this.redisConfig.getBalanceKey(), nickName);
            return money == null ? manager.getConfigFile().getStartCoins() : Double.parseDouble(money);
        }
    }

    @Override
    public boolean hasMoney(String nickName, double amount) {
        return this.getMoneyBalance(nickName) >= amount;
    }

    @Override
    public boolean withdrawMoney(String nickName, double amount) {
        try (Jedis jedis = this.redisPool.getRedis()) {
            jedis.select(this.redisConfig.getDatabase());
            jedis.hset(this.redisConfig.getBalanceKey(), nickName, String.valueOf(amount));
            return true;
        }
    }

    @Override
    public boolean depositMoney(String nickName, double amount) {
        try (Jedis jedis = this.redisPool.getRedis()) {
            jedis.select(this.redisConfig.getDatabase());
            jedis.hset(this.redisConfig.getBalanceKey(), nickName, String.valueOf(amount));
            return true;
        }
    }

    @Override
    public boolean setMoney(String nickName, double amount) {
        try (Jedis jedis = this.redisPool.getRedis()) {
            jedis.select(this.redisConfig.getDatabase());
            jedis.hset(this.redisConfig.getBalanceKey(), nickName, String.valueOf(amount));
            return true;
        }
    }

    @Override
    public int getCoinsBalance(String nickName) {
        try (Jedis jedis = this.redisPool.getRedis()) {
            jedis.select(this.redisConfig.getDatabase());
            String coins = jedis.hget(this.redisConfig.getCoinsKey(), nickName);
            return coins == null ? manager.getConfigFile().getStartCoins() : Integer.parseInt(coins);
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
        try (Jedis jedis = this.redisPool.getRedis()) {
            jedis.select(this.redisConfig.getDatabase());
            jedis.hset(this.redisConfig.getCoinsKey(), nickName, String.valueOf(result));
            return true;
        }
    }

    @Override
    public boolean depositCoins(String nickName, int amount) {
        try (Jedis jedis = this.redisPool.getRedis()) {
            jedis.select(this.redisConfig.getDatabase());
            jedis.hset(this.redisConfig.getCoinsKey(), nickName, String.valueOf(amount));
            return true;
        }
    }

    @Override
    public boolean setCoins(String nickName, int amount) {
        try (Jedis jedis = this.redisPool.getRedis()) {
            jedis.select(this.redisConfig.getDatabase());
            jedis.hset(this.redisConfig.getCoinsKey(), nickName, String.valueOf(amount));
            return true;
        }
    }

    @Override
    public boolean createAccount(String nickName) {
        System.out.println("S-ECON-DEBUG: JsonStorage.class, createAccount method has been called.");
        return true;
    }

    @Override
    public void clearBalances(String nickName) {
        try (Jedis jedis = this.redisPool.getRedis()) {
            jedis.select(this.redisConfig.getDatabase());
            jedis.hdel(this.redisConfig.getBalanceKey(), nickName);
            jedis.hdel(this.redisConfig.getCoinsKey(), nickName);
        }
    }

    @Override
    public List<TopManager.TopLineDouble> getMoneyTop(int amount) {
        Map<String, Double> map = new HashMap<>();
        try (Jedis jedis = this.redisPool.getRedis()) {
            jedis.select(this.redisConfig.getDatabase());
            jedis.hgetAll(this.redisConfig.getBalanceKey()).forEach((s, s2) -> map.put(s, Double.parseDouble(s2)));
        }
        val dataList = new ArrayList<TopManager.TopLineDouble>();
        //map.entrySet().stream().sorted(Map.Entry.comparingByValue()).limit(amount).collect(Collectors.toList()).forEach(entry -> dataList.add(new TopManager.TopLineDouble()));
        return dataList;
    }

    @Override
    public List<TopManager.TopLineInteger> getCoinsTop(int amount) {
        /*TObjectIntMap<String> map = new TObjectIntHashMap<>();
        try (Jedis jedis = this.redisPool.getRedis()) {
            jedis.select(this.redisConfig.getDatabase());
            jedis.hgetAll(this.redisConfig.getCoinsKey()).forEach((nickName, balance) -> map.put(nickName, Integer.parseInt(balance)));
        }*/
        val dataList = new ArrayList<TopManager.TopLineInteger>();
       /* map.forEachEntry((nickName, sum) -> {
            dataList.add(new TopManager.TopLineInteger(nickName, sum));
            return true;
        });
        dataList.sort(Comparator.comparingDouble(TopManager.TopLineInteger::getSum).reversed());*/
        return dataList;
    }

    @Override
    public void close() {
        this.redisPool.close();
    }
}
