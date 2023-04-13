package ua.klesaak.simpleconomy.storage;

import ua.klesaak.simpleconomy.manager.PlayerData;

import java.util.Collection;

public interface IStorage {
    void init();
    void savePlayer(String nickName, PlayerData playerData);
    boolean hasAccount(String nickName);

    //================VAULT================\\
    double getMoneyBalance(String nickName);
    boolean hasMoney(String nickName, double amount);
    boolean withdrawMoney(String nickName, double amount);
    boolean depositMoney(String nickName, double amount);

    //================COINS================\\
    int getCoinsBalance(String nickName);
    boolean hasCoins(String nickName, int amount);
    boolean withdrawCoins(String nickName, int amount);
    boolean depositCoins(String nickName, int amount);
    //================COINS================\\
    boolean createAccount(String nickName);
    PlayerData getPlayer(String nickName);

    Collection<PlayerData> getMoneyTop(int amount);
    Collection<PlayerData> getCoinsTop(int amount);

    void close();
}
