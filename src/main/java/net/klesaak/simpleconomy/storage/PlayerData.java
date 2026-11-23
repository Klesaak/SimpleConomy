package net.klesaak.simpleconomy.storage;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerData {
    private final AtomicInteger money;
    private final AtomicInteger coins;

    public PlayerData(int money, int coins) {
        this.money = new AtomicInteger(money);
        this.coins = new AtomicInteger(coins);
    }

    public void withdrawMoney(int amount) {
        this.money.getAndAdd(-amount);
    }

    public void depositMoney(int amount) {
        this.money.getAndAdd(amount);
    }

    public void setMoney(int amount) {
        this.money.set(amount);
    }

    public void withdrawCoins(int amount) {
        this.coins.getAndAdd(-amount);
    }

    public void depositCoins(int amount) {
        this.coins.getAndAdd(amount);
    }

    public int getCoins() {
        return this.coins.get();
    }

    public void setCoins(int amount) {
        this.coins.set(amount);
    }

    public int getMoney() {
        return this.money.get();
    }
}