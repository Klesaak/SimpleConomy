package ua.klesaak.simpleconomy.api;

import lombok.experimental.UtilityClass;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.storage.AbstractStorage;

@UtilityClass
public class SimpleEconomyAPI {
    private AbstractStorage STORAGE;

    public void register(SimpleEconomyManager manager) {
        STORAGE = manager.getStorage();
    }

    public double getMoneyBalance(String nickName) {
        return STORAGE.getMoneyBalance(nickName.toLowerCase());
    }

    public int getCoinsBalance(String nickName) {
        return STORAGE.getCoinsBalance(nickName.toLowerCase());
    }

    public boolean hasMoney(String nickName, double amount) {
        return STORAGE.hasMoney(nickName.toLowerCase(), amount);
    }

    public boolean hasCoins(String nickName, int amount) {
        return STORAGE.hasCoins(nickName.toLowerCase(), amount);
    }

    public void withdrawMoney(String nickName, double amount) {
        STORAGE.withdrawMoney(nickName.toLowerCase(), amount);
    }

    public void depositMoney(String nickName, double amount) {
        STORAGE.depositMoney(nickName.toLowerCase(), amount);
    }

    public void withdrawCoins(String nickName, int amount) {
        STORAGE.withdrawCoins(nickName.toLowerCase(), amount);
    }

    public void depositCoins(String nickName, int amount) {
        STORAGE.depositCoins(nickName.toLowerCase(), amount);
    }
}