package ua.klesaak.simpleconomy.storage.file;

import com.google.gson.reflect.TypeToken;
import lombok.Synchronized;
import org.bukkit.Bukkit;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.manager.TopManager;
import ua.klesaak.simpleconomy.storage.AbstractStorage;
import ua.klesaak.simpleconomy.utils.JsonData;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JsonStorage extends AbstractStorage {
    public static TypeToken<Map<String, PlayerData>> DATA_COLLECTION_TYPE = new TypeToken<>() {};

    private final Map<String, PlayerData> playersCache = new ConcurrentHashMap<>(Bukkit.getMaxPlayers());
    private final JsonData storageFile;
    public JsonStorage(SimpleEconomyManager manager) {
        super(manager);
        this.storageFile = new JsonData(new File(this.manager.getPlugin().getDataFolder(), "storage.json"));
        if (storageFile.getFile().length() > 0L) {
            this.playersCache.putAll(storageFile.readAll(DATA_COLLECTION_TYPE));
        }
    }

    @Synchronized
    private void save() {
        CompletableFuture.runAsync(() -> this.storageFile.write(this.playersCache, DATA_COLLECTION_TYPE.getType()));
    }

    @Override
    public boolean hasAccount(String nickName) {
        return this.playersCache.containsKey(nickName);
    }

    @Override
    public double getMoneyBalance(String nickName) {
        if (this.hasAccount(nickName)) return this.playersCache.get(nickName).getMoney();
        return manager.getConfigFile().getStartBalance();
    }

    @Override
    public boolean hasMoney(String nickName, double amount) {
        if (this.hasAccount(nickName)) return this.playersCache.get(nickName).getMoney() >= amount;
        return manager.getConfigFile().getStartBalance() >= amount;
    }

    @Override
    public boolean withdrawMoney(String nickName, double amount) {
        if (this.hasAccount(nickName)) {
            this.playersCache.get(nickName).withdrawMoney(amount);
            this.save();
            return true;
        }
        var playerData =  new PlayerData(manager.getConfigFile().getStartBalance(), manager.getConfigFile().getStartCoins());
        playerData.withdrawMoney(amount);
        this.playersCache.put(nickName, playerData);
        this.save();
        return true;
    }

    @Override
    public boolean depositMoney(String nickName, double amount) {
        if (this.hasAccount(nickName)) {
            this.playersCache.get(nickName).depositMoney(amount);
            this.save();
            return true;
        }
        var playerData =  new PlayerData(manager.getConfigFile().getStartBalance(), manager.getConfigFile().getStartCoins());
        playerData.depositMoney(amount);
        this.playersCache.put(nickName, playerData);
        this.save();
        return true;
    }

    @Override
    public boolean setMoney(String nickName, double amount) {
        if (this.hasAccount(nickName)) {
            this.playersCache.get(nickName).setMoney(amount);
            this.save();
            return true;
        }
        this.playersCache.put(nickName, new PlayerData(amount, manager.getConfigFile().getStartCoins()));
        this.save();
        return true;
    }

    @Override
    public int getCoinsBalance(String nickName) {
        if (this.hasAccount(nickName)) return this.playersCache.get(nickName).getCoins();
        return manager.getConfigFile().getStartCoins();
    }

    @Override
    public boolean hasCoins(String nickName, int amount) {
        if (this.hasAccount(nickName)) return this.playersCache.get(nickName).getCoins() >= amount;
        return manager.getConfigFile().getStartCoins() >= amount;
    }

    @Override
    public boolean withdrawCoins(String nickName, int amount) {
        if (this.hasAccount(nickName)) {
            this.playersCache.get(nickName).withdrawCoins(amount);
            this.save();
            return true;
        }
        var playerData =  new PlayerData(manager.getConfigFile().getStartBalance(), manager.getConfigFile().getStartCoins());
        playerData.withdrawCoins(amount);
        this.playersCache.put(nickName, playerData);
        this.save();
        return true;
    }

    @Override
    public boolean depositCoins(String nickName, int amount) {
        if (this.hasAccount(nickName)) {
            this.playersCache.get(nickName).depositCoins(amount);
            this.save();
            return true;
        }
        var playerData =  new PlayerData(manager.getConfigFile().getStartBalance(), manager.getConfigFile().getStartCoins());
        playerData.depositCoins(amount);
        this.playersCache.put(nickName, playerData);
        this.save();
        return true;
    }

    @Override
    public boolean setCoins(String nickName, int amount) {
        if (this.hasAccount(nickName)) {
            this.playersCache.get(nickName).setCoins(amount);
            this.save();
            return true;
        }
        this.playersCache.put(nickName, new PlayerData(manager.getConfigFile().getStartBalance(), amount));
        this.save();
        return true;
    }

    @Override
    public boolean createAccount(String nickName) {
        System.out.println("S-ECON-DEBUG: JsonStorage.class, createAccount method has been called.");
        return true;
    }

    @Override
    public void clearBalances(String nickName) {
        this.playersCache.remove(nickName);
        this.save();
    }

    @Override
    public List<TopManager.TopLineDouble> getMoneyTop(int amount) {
        Map<String, Double> map = new HashMap<>();
        this.playersCache.forEach((nickName, playerData) -> map.put(nickName, playerData.getMoney()));
        var sortedList = map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        Collections.reverse(sortedList);
        var data = new ArrayList<TopManager.TopLineDouble>(amount);
        int dataListSize = sortedList.size();
        for (int i = 0; i < amount && dataListSize != i; i++) {
            var entry = sortedList.get(i);
            data.add(new TopManager.TopLineDouble(entry.getKey(), entry.getValue(), i+1));
        }
        return data;
    }

    @Override
    public List<TopManager.TopLineInteger> getCoinsTop(int amount) {
        Map<String, Integer> map = new HashMap<>();
        this.playersCache.forEach((nickName, playerData) -> map.put(nickName, playerData.getCoins()));
        var sortedList = map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        Collections.reverse(sortedList);
        var data = new ArrayList<TopManager.TopLineInteger>(amount);
        int dataListSize = sortedList.size();
        for (int i = 0; i < amount && dataListSize != i; i++) {
            var entry = sortedList.get(i);
            data.add(new TopManager.TopLineInteger(entry.getKey(), entry.getValue(), i+1));
        }
        return data;
    }

    @Override
    public void close() {
        this.save();
    }
}
