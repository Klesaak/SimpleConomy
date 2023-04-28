package ua.klesaak.simpleconomy.storage;

import ua.klesaak.simpleconomy.manager.PlayerData;

import java.util.List;

public interface IStorage {
    void savePlayer(String nickName, PlayerData playerData);
    void cachePlayer(String nickName);
    void unCachePlayer(String nickName);
    boolean hasAccount(String nickName);

    //================VAULT================\\
    double getMoneyBalance(String nickName);
    boolean hasMoney(String nickName, double amount);
    boolean withdrawMoney(String nickName, double amount);
    boolean depositMoney(String nickName, double amount);
    boolean setMoney(String nickName, double amount);

    //================COINS================\\
    int getCoinsBalance(String nickName);
    boolean hasCoins(String nickName, int amount);
    boolean withdrawCoins(String nickName, int amount);
    boolean depositCoins(String nickName, int amount);
    boolean setCoins(String nickName, int amount);
    //================COINS================\\
    boolean createAccount(String nickName);
    PlayerData getPlayer(String nickName);

    void deleteAccount(String nickName);

    List<String> getMoneyTop(int amount);
    List<String> getCoinsTop(int amount);

    void close();
}
