package ua.klesaak.simpleconomy.storage;

import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.manager.TopManager;

import java.util.List;

public abstract class AbstractStorage implements AutoCloseable {
    protected final SimpleEconomyManager manager;

    public AbstractStorage(SimpleEconomyManager manager) {
        this.manager = manager;
    }

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

    public abstract List<TopManager.TopLineDouble> getMoneyTop(int amount);
    public abstract List<TopManager.TopLineInteger> getCoinsTop(int amount);

    @Override
    public abstract void close();
}
