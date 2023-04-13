package ua.klesaak.simpleconomy.manager;

import lombok.Getter;
import org.bukkit.Bukkit;
import ua.klesaak.simpleconomy.SimpleConomyPlugin;
import ua.klesaak.simpleconomy.commands.AdminCommands;
import ua.klesaak.simpleconomy.configurations.ConfigFile;
import ua.klesaak.simpleconomy.papi.PAPIExpansion;
import ua.klesaak.simpleconomy.storage.IStorage;
import ua.klesaak.simpleconomy.vault.VaultEconomyHook;

import java.util.logging.Level;

@Getter
public class SimpleEconomyManager {
    private final SimpleConomyPlugin plugin;
    private ConfigFile configFile;
    private IStorage storage;

    public SimpleEconomyManager(SimpleConomyPlugin plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            this.plugin.getLogger().log(Level.SEVERE, "Vault is not found! Disabling...");
            Bukkit.getPluginManager().disablePlugin(this.plugin);
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new PAPIExpansion(this);
        this.configFile = new ConfigFile(this.plugin);
        new VaultEconomyHook(this);

        //================COMMANDS================\\
        new AdminCommands(this);
    }

    public void disable() {

    }
}
