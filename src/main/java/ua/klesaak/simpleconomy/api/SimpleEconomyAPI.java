package ua.klesaak.simpleconomy.api;

import lombok.experimental.UtilityClass;
import lombok.val;
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
        String nickNameLC = nickName.toLowerCase();
        double result = getMoneyBalance(nickNameLC) - amount;
        if (result < 0) result = 0;
        MANAGER.getStorage().withdrawMoney(nickNameLC, result);
    }

    public void depositMoney(String nickName, double amount) {
        String nickNameLC = nickName.toLowerCase();
        double result = getMoneyBalance(nickNameLC) + amount;
        val maxBalance = MANAGER.getConfigFile().getMaxBalance();
        if (result > maxBalance) result = maxBalance;
        MANAGER.getStorage().depositMoney(nickNameLC, result);
    }

    public void withdrawCoins(String nickName, int amount) {
        String nickNameLC = nickName.toLowerCase();
        int result = getCoinsBalance(nickNameLC) - amount;
        if (result < 0) result = 0;
        MANAGER.getStorage().withdrawCoins(nickNameLC, result);
    }

    public void depositCoins(String nickName, int amount) {
        String nickNameLC = nickName.toLowerCase();
        int result = getCoinsBalance(nickNameLC) + amount;
        val maxBalance = MANAGER.getConfigFile().getMaxCoins();
        if (result > maxBalance) result = maxBalance;
        MANAGER.getStorage().depositCoins(nickNameLC, result);
    }
}