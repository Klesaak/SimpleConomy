package net.klesaak.simpleconomy.storage.mesenger;

public enum MessageType {
    WITHDRAW_MONEY,
    DEPOSIT_MONEY,
    SET_MONEY,
    WITHDRAW_COINS,
    DEPOSIT_COINS,
    SET_COINS,
    UNKNOWN;

    public boolean isMoneyUpdate() {
        return this == WITHDRAW_MONEY || this == DEPOSIT_MONEY || this == SET_MONEY;
    }

    public boolean isCoinsUpdate() {
        return this == WITHDRAW_COINS || this == DEPOSIT_COINS || this == SET_COINS;
    }
}
