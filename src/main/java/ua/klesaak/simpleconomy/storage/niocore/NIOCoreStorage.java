package ua.klesaak.simpleconomy.storage.niocore;

import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.storage.AbstractStorage;
import ua.klesaak.simpleconomy.storage.PlayerData;

import java.util.List;

public class NIOCoreStorage extends AbstractStorage {

    public NIOCoreStorage(SimpleEconomyManager manager) {
        super(manager);
    }

    @Override
    public void savePlayer(String nickName, PlayerData playerData) {

    }

    @Override
    public void cachePlayer(String nickName) {

    }

    @Override
    public void unCachePlayer(String nickName) {

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
    public boolean setMoney(String nickName, double amount) {
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
    public boolean setCoins(String nickName, int amount) {
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
    public void deleteAccount(String nickName) {

    }

    @Override
    public List<String> getMoneyTop(int amount) {
        return null;
    }

    @Override
    public List<String> getCoinsTop(int amount) {
        return null;
    }

    @Override
    public void close() {

    }
}
