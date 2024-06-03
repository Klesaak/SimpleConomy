package ua.klesaak.simpleconomy.manager;

import lombok.Getter;
import lombok.val;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;
import ua.klesaak.simpleconomy.SimpleConomyPlugin;
import ua.klesaak.simpleconomy.api.SimpleEconomyAPI;
import ua.klesaak.simpleconomy.commands.AdminCommands;
import ua.klesaak.simpleconomy.commands.BalTopCommand;
import ua.klesaak.simpleconomy.commands.BalanceCommand;
import ua.klesaak.simpleconomy.commands.PayCommand;
import ua.klesaak.simpleconomy.configurations.ConfigFile;
import ua.klesaak.simpleconomy.configurations.MessagesFile;
import ua.klesaak.simpleconomy.papi.PAPIExpansion;
import ua.klesaak.simpleconomy.storage.AbstractStorage;
import ua.klesaak.simpleconomy.storage.file.JsonStorage;
import ua.klesaak.simpleconomy.storage.redis.RedisStorage;
import ua.klesaak.simpleconomy.vault.VaultEconomyHook;

//todo нормальный reload
//todo redis

@Getter
public class SimpleEconomyManager {
    private final SimpleConomyPlugin plugin;
    private ConfigFile configFile;
    private MessagesFile messagesFile;
    private volatile AbstractStorage storage;
    private TopManager topManager;
    private PAPIExpansion papiExpansion;

    public SimpleEconomyManager(SimpleConomyPlugin plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            Bukkit.getServicesManager().register(Economy.class, new VaultEconomyHook(this), this.plugin, ServicePriority.Highest);
        }
        this.configFile = new ConfigFile(this.plugin);
        this.messagesFile = new MessagesFile(this.plugin);
        this.initStorage();
        //================COMMANDS================\\
        new AdminCommands(this);
        new BalanceCommand(this);
        if (this.configFile.isPayCommandEnabled()) new PayCommand(this);
        //================COMMANDS================\\
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.papiExpansion = new PAPIExpansion(this);
            this.papiExpansion.register();
        }
        SimpleEconomyAPI.register(this);
        if (this.configFile.isTopEnabled()) {
            this.topManager = new TopManager(this, this.configFile);
            new BalTopCommand(this);
        }
    }

    private void initStorage() {
        val storageType = this.configFile.getStorageType();
        switch (storageType) {
            case FILE: {
                this.storage = new JsonStorage(this);
                break;
            }
            case REDIS: {
                this.storage = new RedisStorage(this);
                break;
            }
            default: {
                this.storage = new JsonStorage(this);
            }
        }
        this.getPlugin().getLogger().info("Storage type loaded: " + storageType);
    }

    public String reload() {
        this.configFile = new ConfigFile(this.plugin);
        this.messagesFile = new MessagesFile(this.plugin);
        return ChatColor.GREEN + this.getPlugin().getDescription().getName() + " reloaded!";
    }

    public void disable() {
        this.storage.close();
        if (this.plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && this.papiExpansion.isRegistered()) {
            this.papiExpansion.unregister();
        }
        this.topManager.close();
    }
}
