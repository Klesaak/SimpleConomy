package ua.klesaak.simpleconomy.storage.file;

import com.google.gson.reflect.TypeToken;
import lombok.Synchronized;
import org.bukkit.Bukkit;
import ua.klesaak.simpleconomy.manager.PlayerData;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.storage.IStorage;
import ua.klesaak.simpleconomy.utils.JsonData;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class JsonStorage implements IStorage {
    private final SimpleEconomyManager manager;
    Map<String, PlayerData> playersCache = new ConcurrentHashMap<>(Bukkit.getMaxPlayers());
    private JsonData storage;

    public JsonStorage(SimpleEconomyManager manager) {
        this.manager = manager;
        this.storage = new JsonData(new File(this.manager.getPlugin().getDataFolder(), "storage.json"));
        if (storage.getFile().length() > 0L) {
            this.playersCache = storage.readAll(new TypeToken<Map<String, PlayerData>>(){});
        }
    }


    @Override
    public void init() {

    }

    @Override @Synchronized
    public void savePlayer(String nickName, PlayerData playerData) {
        CompletableFuture.runAsync(() -> this.storage.write(this.playersCache, true));
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

        return true;
    }

    @Override
    public boolean depositMoney(String nickName, double amount) {

        return false;
    }

    @Override
    public int getCoinsBalance(String nickName) {
        return 0;
    }

    @Override
    public boolean hasCoins(String nickName, int amount) {
        return false;
    }

    @Override
    public boolean withdrawCoins(String nickName, int amount) {
        return false;
    }

    @Override
    public boolean depositCoins(String nickName, int amount) {
        return false;
    }

    @Override
    public boolean createAccount(String nickName) {
        return false;
    }

    @Override
    public PlayerData getPlayer(String nickName) {
        return null;
    }

    @Override
    public Collection<String> getMoneyTop(int amount) {
        return null;
    }

    @Override
    public Collection<String> getCoinsTop(int amount) {
        return null;
    }

    @Override
    public void close() {

    }
}
