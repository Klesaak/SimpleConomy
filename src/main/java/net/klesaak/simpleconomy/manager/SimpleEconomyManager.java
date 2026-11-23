package net.klesaak.simpleconomy.manager;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import net.klesaak.simpleconomy.SimpleConomyPlugin;
import net.klesaak.simpleconomy.api.SimpleEconomyAPI;
import net.klesaak.simpleconomy.commands.AdminCommands;
import net.klesaak.simpleconomy.commands.BalanceCommand;
import net.klesaak.simpleconomy.commands.PayCommand;
import net.klesaak.simpleconomy.configurations.ConfigFile;
import net.klesaak.simpleconomy.configurations.MessagesFile;
import net.klesaak.simpleconomy.papi.PAPIExpansion;
import net.klesaak.simpleconomy.storage.AbstractStorage;
import net.klesaak.simpleconomy.storage.sql.SQLStorage;
import net.klesaak.simpleconomy.vault.VaultEconomyHook;

@Getter
public class SimpleEconomyManager {
    private final SimpleConomyPlugin plugin;
    private final AbstractStorage storage;
    private PAPIExpansion papiExpansion;

    public SimpleEconomyManager(SimpleConomyPlugin plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            Bukkit.getServicesManager().register(Economy.class, new VaultEconomyHook(this), this.plugin, ServicePriority.Highest);
        }
        ConfigFile.reload(plugin);
        MessagesFile.reload(plugin);
        this.storage = new SQLStorage(this, ConfigFile.get());
        //================COMMANDS================\\
        new AdminCommands(this);
        new BalanceCommand(this);
        if (ConfigFile.get().isPayCommandEnabled()) new PayCommand(this);
        //================COMMANDS================\\
        Bukkit.getScheduler().runTaskLater(this.plugin, ()-> {
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                this.papiExpansion = new PAPIExpansion(this);
                this.papiExpansion.register();
            }
        }, 60L);
        SimpleEconomyAPI.register(this);
        new SConomyListener(this);
    }

    public void reload() {
        ConfigFile.reload(this.plugin);
        MessagesFile.reload(this.plugin);
        this.storage.close();
    }

    public void disable() {
        this.storage.close();
        if (this.plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && this.papiExpansion.isRegistered()) {
            this.papiExpansion.unregister();
        }
    }
}