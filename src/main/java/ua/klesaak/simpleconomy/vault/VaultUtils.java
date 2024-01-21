package ua.klesaak.simpleconomy.vault;

import lombok.experimental.UtilityClass;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import ua.klesaak.simpleconomy.utils.NumberUtils;

@UtilityClass
public class VaultUtils {
    private final Economy ECO        = getProvider(Economy.class);

    @SuppressWarnings("deprecation")
    public void addMoney(Player p, double count) {
        if (ECO == null || p == null) return;
        ECO.depositPlayer(p.getName(), count);
    }

    @SuppressWarnings("deprecation")
    public void addMoney(String playerName, double count) {
        if (ECO == null || playerName == null) return;
        ECO.depositPlayer(playerName, count);
    }

    @SuppressWarnings("deprecation")
    public void takeMoney(Player p, double count) {
        if (ECO == null || p == null) return;
        if (getMoney(p) >= count) {
            ECO.withdrawPlayer(p.getName(), count).transactionSuccess();
        }
    }

    @SuppressWarnings("deprecation")
    public int getMoney(Player p) {
        if (ECO == null || p == null) return 0;
        return (int) ECO.getBalance(p.getName());
    }

    @SuppressWarnings("deprecation")
    public int getMoney(String playerName) {
        if (ECO == null || playerName == null) return 0;
        return (int) ECO.getBalance(playerName);
    }

    public String getFormattedMoney(Player p, String symbol) {
        return NumberUtils.spaced(getMoney(p), symbol);
    }

    public String getFormattedMoney(String playerName, String symbol) {
        return NumberUtils.spaced(getMoney(playerName), symbol);
    }

    public boolean hasMoney(String playerName, double moneyCount) {
        return getMoney(playerName) >= moneyCount;
    }

    public void setMoney(String playerName, double moneyCount) {
        double playerBalance = getMoney(playerName);

        if (moneyCount > playerBalance) {
            giveMoney(playerName, moneyCount - playerBalance);
        }

        if (moneyCount < playerBalance) {
            takeMoney(playerName, playerBalance - moneyCount);
        }
    }

    @SuppressWarnings("deprecation")
    public void giveMoney(String playerName, double moneyToGive) {
        if (ECO == null) return;
        ECO.depositPlayer(playerName, moneyToGive);
    }

    @SuppressWarnings("deprecation")
    public void takeMoney(String playerName, double moneyToTake) {
        if (ECO == null) return;
        ECO.withdrawPlayer(playerName, moneyToTake);
    }

    private <P> P getProvider(Class<P> clazz) {
        RegisteredServiceProvider<P> provider = Bukkit.getServer().getServicesManager().getRegistration(clazz);
        return (provider != null) ? provider.getProvider() : null;
    }
}
