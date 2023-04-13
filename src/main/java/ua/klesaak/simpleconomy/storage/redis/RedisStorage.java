package ua.klesaak.simpleconomy.storage.redis;

import ua.klesaak.simpleconomy.manager.PlayerData;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.storage.IStorage;

import java.util.Collection;

public class RedisStorage implements IStorage {
    private final SimpleEconomyManager manager;

    public RedisStorage(SimpleEconomyManager manager) {
        this.manager = manager;
    }


    @Override
    public void init() {

    }

    @Override
    public void savePlayer(String nickName, PlayerData playerData) {

    }

    @Override
    public boolean hasAccount(String nickName) {
        return false;
    }

    @Override
    public double getMoneyBalance(String nickName) {
        return 0;
    }

    @Override
    public boolean hasMoney(String nickName, double amount) {
        return false;
    }

    @Override
    public boolean withdrawMoney(String nickName, double amount) {
        return false;
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
    public Collection<PlayerData> getMoneyTop(int amount) {
        return null;
    }

    @Override
    public Collection<PlayerData> getCoinsTop(int amount) {
        return null;
    }

    @Override
    public void close() {

    }
}
