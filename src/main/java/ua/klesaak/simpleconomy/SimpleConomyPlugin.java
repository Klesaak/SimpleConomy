package ua.klesaak.simpleconomy;

import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.LoadBefore;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependsOn;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.permission.Permissions;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;

@Plugin(name = "SimpleConomy", version = "0.1")
@Author("Klesaak")
@SoftDependsOn({
        @SoftDependency("PlaceholderAPI"),
        @SoftDependency("Vault")
})
@LoadBefore("PlaceholderAPI")
@Commands({
        @Command(name = "sconomy", aliases = {"scon", "seco"}, desc = "Admin command.", permission = "simpleconomy.admin"),
        @Command(name = "balance", aliases = {"money", "emoney", "ebalance", "bal", "ebal", "eco", "coins"}, desc = "Command to check your balance."),
        @Command(name = "baltop", aliases = "ebaltop", desc = "Command to check top balance."),
        @Command(name = "pay", aliases  = {"epay", "wpay"}, desc = "Player money transaction command.")
})
@Description("Simple high performance economy plugin.")
@Permissions({
        @Permission(name = "simpleconomy.admin", defaultValue = PermissionDefault.OP, desc = "Access to use admin command."),
        @Permission(name = "simpleconomy.others", defaultValue = PermissionDefault.OP, desc = "Access to check other player balance.")
})

public class SimpleConomyPlugin extends JavaPlugin {
    private SimpleEconomyManager manager;

    @Override
    public void onEnable() {
        this.manager = new SimpleEconomyManager(this);
    }

    @Override
    public void onDisable() {
        this.manager.disable();
    }
}
