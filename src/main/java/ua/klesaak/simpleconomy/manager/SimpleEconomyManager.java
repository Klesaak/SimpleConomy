package ua.klesaak.simpleconomy.manager;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ua.klesaak.simpleconomy.SimpleConomyPlugin;
import ua.klesaak.simpleconomy.commands.AdminCommands;
import ua.klesaak.simpleconomy.commands.BalanceCommand;
import ua.klesaak.simpleconomy.configurations.ConfigFile;
import ua.klesaak.simpleconomy.configurations.MessagesFile;
import ua.klesaak.simpleconomy.papi.PAPIExpansion;
import ua.klesaak.simpleconomy.storage.IStorage;
import ua.klesaak.simpleconomy.storage.file.JsonStorage;
import ua.klesaak.simpleconomy.storage.mysql.MySQLStorage;
import ua.klesaak.simpleconomy.storage.redis.RedisStorage;
import ua.klesaak.simpleconomy.vault.VaultEconomyHook;

import java.util.logging.Level;

@Getter
public class SimpleEconomyManager implements Listener {
    private final SimpleConomyPlugin plugin;
    private ConfigFile configFile;
    private MessagesFile messagesFile;
    private IStorage storage;
    private TopManager topManager;

    public SimpleEconomyManager(SimpleConomyPlugin plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            this.plugin.getLogger().log(Level.SEVERE, "Vault is not found! Disabling...");
            Bukkit.getPluginManager().disablePlugin(this.plugin);
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new PAPIExpansion(this);
        this.configFile = new ConfigFile(this.plugin);
        this.messagesFile = new MessagesFile(this.plugin);
        new VaultEconomyHook(this);
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        this.initStorage();
        //================COMMANDS================\\
        new AdminCommands(this);
        new BalanceCommand(this);
        this.topManager = new TopManager(this, this.configFile.getPlayerTopMoneyCount(), this.configFile.getPlayerTopCoinsCount(), this.configFile.getPlayerTopUpdateTickInterval());
    }

    private void initStorage() {
        String storageType = this.configFile.getStorage().toLowerCase();
        switch (storageType) {
            case "file": {
                this.storage = new JsonStorage(this);
                break;
            }
            case "mysql": {
                this.storage = new MySQLStorage(this);
                break;
            }
            case "redis": {
                this.storage = new RedisStorage(this);
                break;
            }
            default: {
                this.storage = new JsonStorage(this);
            }
        }
        this.getPlugin().getLogger().info("Storage type loaded: " + storageType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        this.storage.cachePlayer(event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        this.storage.unCachePlayer(event.getPlayer().getName());
    }

    public String reload() {
        this.configFile = new ConfigFile(this.plugin);
        this.messagesFile = new MessagesFile(this.plugin);
        return ChatColor.GREEN + this.getPlugin().getDescription().getName() + " reloaded!";
    }

    public void disable() {
        this.storage.close();
    }
}
