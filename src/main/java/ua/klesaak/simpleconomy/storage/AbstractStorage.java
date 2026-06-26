package ua.klesaak.simpleconomy.storage;

import org.bukkit.Bukkit;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.manager.TopManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractStorage implements AutoCloseable {
    protected final SimpleEconomyManager manager;
    protected final ExecutorService executorService;
    protected final Map<String, PlayerData> playersCache = new ConcurrentHashMap<>(Bukkit.getMaxPlayers());

    public AbstractStorage(SimpleEconomyManager manager, int threads) {
        this.manager = manager;
        this.executorService = Executors.newFixedThreadPool(threads);
    }

    public abstract void cache(String nickName);
    public abstract void unCache(String nickName);

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

    public abstract void clearBalances(String nickName);

    public PlayerData getPlayerData(String nickName) {
        return this.playersCache.get(nickName);
    }

    /**
     *
     * @param amount - кол-во игроков в списке топа.
     * @return - список никнейм-сумма-место в топе. (идут сверху вниз в порядке возрастания)
     */
    public abstract List<TopManager.TopLineDouble> getMoneyTop(int amount);
    public abstract List<TopManager.TopLineInteger> getCoinsTop(int amount);

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    @Override
    public abstract void close();
}
