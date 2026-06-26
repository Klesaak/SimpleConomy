package ua.klesaak.simpleconomy.api;

import lombok.val;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.manager.TopManager;

import java.util.List;

public class SimpleEconomyAPI {
    private static SimpleEconomyAPI INSTANCE;

    private final SimpleEconomyManager manager;

    private SimpleEconomyAPI(SimpleEconomyManager manager) {
        this.manager = manager;
    }

    public static void register(SimpleEconomyManager manager) {
        INSTANCE = new SimpleEconomyAPI(manager);
    }

    public static SimpleEconomyAPI get() {
        return INSTANCE;
    }

    public double getMoneyBalance(String nickName) {
        return manager.getStorage().getMoneyBalance(nickName);
    }

    public int getCoinsBalance(String nickName) {
        return manager.getStorage().getCoinsBalance(nickName);
    }

    public boolean hasMoney(String nickName, double amount) {
        return manager.getStorage().hasMoney(nickName, amount);
    }

    public boolean hasCoins(String nickName, int amount) {
        return manager.getStorage().hasCoins(nickName, amount);
    }

    public void withdrawMoney(String nickName, double amount) {
        double result = getMoneyBalance(nickName) - amount;
        if (result < 0) result = 0;
        manager.getStorage().withdrawMoney(nickName, result);
    }

    public void depositMoney(String nickName, double amount) {
        double result = getMoneyBalance(nickName) + amount;
        val maxBalance = manager.getConfigFile().getMaxBalance();
        if (result > maxBalance) result = maxBalance;
        manager.getStorage().depositMoney(nickName, result);
    }

    public void withdrawCoins(String nickName, int amount) {
        int result = getCoinsBalance(nickName) - amount;
        if (result < 0) result = 0;
        manager.getStorage().withdrawCoins(nickName, result);
    }

    public void depositCoins(String nickName, int amount) {
        int result = getCoinsBalance(nickName) + amount;
        val maxBalance = manager.getConfigFile().getMaxCoins();
        if (result > maxBalance) result = maxBalance;
        manager.getStorage().depositCoins(nickName, result);
    }

    public List<TopManager.TopLineDouble> getMoneyTop(int amount) {
        return manager.getStorage().getMoneyTop(amount);
    }
    public List<TopManager.TopLineInteger> getCoinsTop(int amount) {
        return manager.getStorage().getCoinsTop(amount);
    }
}