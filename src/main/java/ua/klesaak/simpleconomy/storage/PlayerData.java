package ua.klesaak.simpleconomy.storage;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerData {
    private double money;
    private int coins;

    public PlayerData(double money, int coins) {
        this.money = money;
        this.coins = coins;
    }

    public PlayerData() {
    }

    public void withdrawMoney(double amount) {
        this.money -= amount;
    }

    public void depositMoney(double amount) {
        this.money += amount;
    }

    public void withdrawCoins(int amount) {
        this.coins -= amount;
    }

    public void depositCoins(int amount) {
        this.coins += amount;
    }
}