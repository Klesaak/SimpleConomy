package ua.klesaak.simpleconomy.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.utils.UtilityMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdminCommands implements CommandExecutor, TabCompleter {
    private static final List<String> SUB_COMMANDS0 = Arrays.asList("reload", "addmoney", "addcoins", "wmoney", "wcoins", "setmoney", "setcoins", "delacc");
    private final SimpleEconomyManager manager;

    public AdminCommands(SimpleEconomyManager manager) {
        this.manager = manager;
        this.manager.getPlugin().getCommand("sconomy").setExecutor(this);
        this.manager.getPlugin().getCommand("sconomy").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("simpleconomy.admin")) {
            sender.sendMessage(ChatColor.GOLD + manager.getPlugin().getDescription().getName() + " v" + manager.getPlugin().getDescription().getVersion() + ChatColor.RED + " by Klesaak");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + manager.getPlugin().getDescription().getName() + " v" + manager.getPlugin().getDescription().getVersion() + ChatColor.RED + " by Klesaak");
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " reload - перезагрузить конфиги.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " addmoney <ник> <сумма> - выдать деньги.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " addcoins <ник> <сумма> - выдать коины.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " wmoney <ник> <сумма> - забрать деньги.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " wcoins <ник> <сумма> - забрать коины.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " setmoney <ник> <сумма> - установить деньги.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " setcoins <ник> <сумма> - установить коины.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " delacc <ник> - удалить аккаунт.");
            return true;
        }

        switch (args[0]) {
            case "reload": {
                sender.sendMessage(ChatColor.GREEN + manager.getPlugin().getDescription().getName() + " reloaded!");
                break;
            }
            case "addmoney": {

                break;
            }
            case "addcoins": {
                System.out.println("addcoins");
                break;
            }
            case "setmoney": {
                System.out.println("setmoney");
                break;
            }
            case "setcoins": {
                System.out.println("setcoins");
                break;
            }
            case "wmoney": {
                System.out.println("wmoney");
                break;
            }
            case "wcoins": {
                System.out.println("wcoins");
                break;
            }
            case "delacc": {
                System.out.println("delacc");
                break;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return UtilityMethods.copyPartialMatches(args[0].toLowerCase(), SUB_COMMANDS0, new ArrayList<>());
        }
        return Collections.emptyList();
    }
}
