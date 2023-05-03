package ua.klesaak.simpleconomy.storage.file;

import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.val;
import ua.klesaak.simpleconomy.manager.PlayerData;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.storage.IStorage;
import ua.klesaak.simpleconomy.utils.JsonData;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JsonStorage implements IStorage {
    SimpleEconomyManager manager;
    Map<String, PlayerData> playersCache = new ConcurrentHashMap<>();
    JsonData storage;

    public JsonStorage(SimpleEconomyManager manager) {
        this.manager = manager;
        this.storage = new JsonData(new File(this.manager.getPlugin().getDataFolder(), "storage.json"));
        if (storage.getFile().length() > 0L) {
            Collection<PlayerData> dataCollection = storage.readAll(new TypeToken<Collection<PlayerData>>() {});
            dataCollection.forEach(playerData -> this.playersCache.put(playerData.getPlayerName(), playerData));
        }
    }

    @Synchronized
    private void save() {
        CompletableFuture.runAsync(() -> this.storage.write(this.playersCache.values(), true));
    }

    @Override
    public void savePlayer(String nickName, PlayerData playerData) {
        this.save();
    }

    @Override
    public void cachePlayer(String nickName) {
        //empty
    }

    @Override
    public void unCachePlayer(String nickName) {
        //empty
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
        val playerData =  new PlayerData(nickName, manager.getConfigFile().getStartBalance(), manager.getConfigFile().getStartCoins());
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
        val playerData =  new PlayerData(nickName, manager.getConfigFile().getStartBalance(), manager.getConfigFile().getStartCoins());
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
        this.playersCache.put(nickName, new PlayerData(nickName, amount, manager.getConfigFile().getStartCoins()));
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
        val playerData =  new PlayerData(nickName, manager.getConfigFile().getStartBalance(), manager.getConfigFile().getStartCoins());
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
        val playerData =  new PlayerData(nickName, manager.getConfigFile().getStartBalance(), manager.getConfigFile().getStartCoins());
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
        this.playersCache.put(nickName, new PlayerData(nickName, manager.getConfigFile().getStartBalance(), amount));
        this.save();
        return true;
    }

    @Override
    public boolean createAccount(String nickName) {
        System.out.println("S-ECON-DEBUG: JsonStorage.class, createAccount method has been called.");
        return true;
    }

    @Override
    public PlayerData getPlayer(String nickName) {
        if (this.hasAccount(nickName)) {
            return this.playersCache.get(nickName);
        }
        return new PlayerData(nickName, manager.getConfigFile().getStartBalance(), manager.getConfigFile().getStartCoins());
    }

    @Override
    public void deleteAccount(String nickName) {
        this.playersCache.remove(nickName);
        this.save();
    }

    @Override
    public List<String> getMoneyTop(int amount) {
        List<String> list = new ArrayList<>(128);
        List<PlayerData> dataList = new ArrayList<>(this.playersCache.values());
        dataList.sort(Comparator.comparingDouble(PlayerData::getMoney).reversed());
        for (val data : dataList) {
            list.add(this.manager.getConfigFile().formatTopLine(String.valueOf(list.size()+1), data.getPlayerName(), String.valueOf(data.getMoney())));
        }
        return list;
    }

    @Override
    public List<String> getCoinsTop(int amount) {
        List<String> list = new ArrayList<>(128);
        List<PlayerData> dataList = new ArrayList<>(this.playersCache.values());
        dataList.sort(Comparator.comparingDouble(PlayerData::getCoins).reversed());
        for (val data : dataList) {
            list.add(this.manager.getConfigFile().formatTopLine(String.valueOf(list.size()+1), data.getPlayerName(), String.valueOf(data.getCoins())));
        }
        Collections.reverse(list);
        return list;
    }

    @Override
    public void close() {
        //empty
    }
}
