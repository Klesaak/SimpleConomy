package ua.klesaak.simpleconomy.api;

import lombok.experimental.UtilityClass;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;

@UtilityClass
public class SimpleEconomyAPI {
    private SimpleEconomyManager MANAGER;

    public void register(SimpleEconomyManager manager) {
        MANAGER = manager;
    }

    public double getMoneyBalance(String nickName) {
        return MANAGER.getStorage().getMoneyBalance(nickName.toLowerCase());
    }

    public int getCoinsBalance(String nickName) {
        return MANAGER.getStorage().getCoinsBalance(nickName.toLowerCase());
    }

    public boolean hasMoney(String nickName, double amount) {
        return MANAGER.getStorage().hasMoney(nickName.toLowerCase(), amount);
    }

    public boolean hasCoins(String nickName, int amount) {
        return MANAGER.getStorage().hasCoins(nickName.toLowerCase(), amount);
    }

    public void withdrawMoney(String nickName, double amount) {
        MANAGER.getStorage().withdrawMoney(nickName.toLowerCase(), amount);
    }

    public void depositMoney(String nickName, double amount) {
        MANAGER.getStorage().depositMoney(nickName.toLowerCase(), amount);
    }

    public void withdrawCoins(String nickName, int amount) {
        MANAGER.getStorage().withdrawCoins(nickName.toLowerCase(), amount);
    }

    public void depositCoins(String nickName, int amount) {
        MANAGER.getStorage().depositCoins(nickName.toLowerCase(), amount);
    }
}