package ua.klesaak.simpleconomy.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.utils.UtilityMethods;

import java.util.Collections;
import java.util.List;

import static ua.klesaak.simpleconomy.utils.UtilityMethods.getOfflinePlayer;

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
        return manager.getMainConfig().getFractionalDigits();
    }

    @Override
    public String format(double amount) {
        return manager.getMainConfig().format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return manager.getMainConfig().getCurrencyPlural();
    }

    @Override
    public String currencyNameSingular() {
        return manager.getMainConfig().getCurrencySingular();
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return manager.getEconomyHandler().hasAccount(UtilityMethods.getUniqueId(player));
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return manager.getEconomyHandler().get(UtilityMethods.getUniqueId(player));
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return manager.getEconomyHandler().has(UtilityMethods.getUniqueId(player), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return manager.getEconomyHandler().withdraw(UtilityMethods.getUniqueId(player), amount)
                ? new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, "Successful")
                : new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Failed to withdraw");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return manager.getEconomyHandler().deposit(UtilityMethods.getUniqueId(player), amount)
                ? new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, "Successful")
                : new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Failed to deposit");
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return manager.getEconomyHandler().createAccount(UtilityMethods.getUniqueId(player));
    }

    //region Expanded Methods
    @Override
    public boolean hasAccount(String playerName) {
        return hasAccount(getOfflinePlayer(playerName));
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        return getBalance(getOfflinePlayer(playerName));
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(getOfflinePlayer(playerName));
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return has(getOfflinePlayer(playerName), amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return createPlayerAccount(getOfflinePlayer(playerName));
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }
    //endregion

    //region Bank (unused)
    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }
    //endregion
}
