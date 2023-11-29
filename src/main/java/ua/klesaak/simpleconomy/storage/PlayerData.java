package ua.klesaak.simpleconomy.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerData {
    private String playerName;
    private double money;
    private int coins;

    public PlayerData(String playerName) {
        this.playerName = playerName;
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