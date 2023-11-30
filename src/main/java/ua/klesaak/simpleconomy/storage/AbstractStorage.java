package ua.klesaak.simpleconomy.storage;

import org.bukkit.Bukkit;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractStorage implements AutoCloseable {
    protected final Map<String, PlayerData> playersCache = new ConcurrentHashMap<>(Bukkit.getMaxPlayers());
    protected final SimpleEconomyManager manager;

    public AbstractStorage(SimpleEconomyManager manager) {
        this.manager = manager;
    }

    public abstract void savePlayer(String nickName, PlayerData playerData);
    public abstract void cachePlayer(String nickName);
    public abstract void unCachePlayer(String nickName);
    public abstract boolean hasAccount(String nickName);

    //================VAULT================\\
    public abstract double getMoneyBalance(String nickName);
    public abstract boolean hasMoney(String nickName, double amount);
    public abstract boolean withdrawMoney(String nickName, double amount);
    public abstract boolean depositMoney(String nickName, double amount);
    public abstract boolean setMoney(String nickName, double amount);

    //================COINS================\\
    public abstract int getCoinsBalance(String nickName);
    public abstract boolean hasCoins(String nickName, int amount);
    public abstract boolean withdrawCoins(String nickName, int amount);
    public abstract boolean depositCoins(String nickName, int amount);
    public abstract boolean setCoins(String nickName, int amount);
    //================COINS================\\
    public abstract boolean createAccount(String nickName);
    public abstract PlayerData getPlayer(String nickName);

    public abstract void deleteAccount(String nickName);

    public abstract List<String> getMoneyTop(int amount);
    public abstract List<String> getCoinsTop(int amount);

    @Override
    public abstract void close();
}
