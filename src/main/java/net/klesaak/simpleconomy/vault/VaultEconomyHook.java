package net.klesaak.simpleconomy.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import net.klesaak.simpleconomy.configurations.ConfigFile;
import net.klesaak.simpleconomy.manager.SimpleEconomyManager;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class VaultEconomyHook implements Economy {
    private final SimpleEconomyManager manager;

    public VaultEconomyHook(SimpleEconomyManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean isEnabled() {
        return manager.getPlugin().isEnabled();
    }

    @Override
    public String getName() {
        return manager.getPlugin().getName();
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return String.valueOf(Math.round(amount));
    }

    @Override
    public String currencyNamePlural() {
        return ConfigFile.get().getCurrencyFormatPlural();
    }

    @Override
    public String currencyNameSingular() {
        return ConfigFile.get().getCurrencyFormatSingular();
    }

    @Override
    public double getBalance(String playerName) {
        return manager.getStorage().getMoneyBalance(Objects.requireNonNull(playerName));
    }

    @Override
    public boolean has(String playerName, double amount) {
        return manager.getStorage().hasMoney(Objects.requireNonNull(playerName), (int) Math.round(amount));
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (this.getBalance(playerName) - amount < 0)
            return new EconomyResponse(amount, this.getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Failed to withdraw");

        return manager.getStorage().withdrawMoney(Objects.requireNonNull(playerName), (int) Math.round(amount))
                ? new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "Successful")
                : new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Failed to withdraw");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (this.getBalance(playerName) + amount > ConfigFile.get().getMaxBalance())
            return new EconomyResponse(amount, this.getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Failed to deposit");
        return manager.getStorage().depositMoney(Objects.requireNonNull(playerName), (int) Math.round(amount))
                ? new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "Successful")
                : new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Failed to deposit");
    }

    //////////////////////////////////////////////////////////////////////////////unused///////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return true;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return false;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return 0.0;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return false;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return false;
    }

    @Override
    public double getBalance(String playerName, String world) {
        return 0.0;
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return 0.0;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return false;
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0.0F,0.0F, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Функция не поддерживается.");
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }
}
