package ua.klesaak.simpleconomy.commands;

import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.utils.AbstractBukkitCommand;
import ua.klesaak.simpleconomy.utils.UtilityMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdminCommands extends AbstractBukkitCommand implements TabCompleter {
    private static final List<String> SUB_COMMANDS0 = Arrays.asList("reload", "addmoney", "addcoins", "wmoney", "wcoins", "setmoney", "setcoins", "delacc");
    private final SimpleEconomyManager manager;

    public AdminCommands(SimpleEconomyManager manager) {
        this.manager = manager;
        this.manager.getPlugin().getCommand("sconomy").setExecutor(this);
        this.manager.getPlugin().getCommand("sconomy").setTabCompleter(this);
    }


    @Override
    public void onReceiveCommand(CommandSender sender, Command command, String[] args) {
        this.cmdVerifyPermission(sender, "simpleconomy.admin", ChatColor.RED + "Нет прав.");
        String label = command.getLabel();
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
            return;
        }
        switch (args[0].toLowerCase()) {
            case "reload": {
                sender.sendMessage(this.manager.reload());
                break;
            }
            case "addmoney": {
                this.cmdVerify(args.length != 3, "§6/" + label + " addmoney <ник> <сумма> - выдать деньги.");

                break;
            }
            case "addcoins": {
                this.cmdVerify(args.length != 3, "§6/" + label + " addcoins <ник> <сумма> - выдать коины.");

                break;
            }
            case "setmoney": {
                this.cmdVerify(args.length != 3, "§6/" + label + " setmoney <ник> <сумма> - установить деньги.");

                break;
            }
            case "setcoins": {
                this.cmdVerify(args.length != 3,"§6/" + label + " setcoins <ник> <сумма> - установить коины.");

                break;
            }
            case "wmoney": {
                this.cmdVerify(args.length != 3, "§6/" + label + " wmoney <ник> <сумма> - забрать деньги.");

                break;
            }
            case "wcoins": {
                this.cmdVerify(args.length != 3,"§6/" + label + " wcoins <ник> <сумма> - забрать коины.");

                break;
            }
            case "delacc": {
                this.cmdVerify(args.length != 2, "§6/" + label + " delacc <ник> - удалить аккаунт.");

                break;
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length == 1) {
            return UtilityMethods.copyPartialMatches(args[0].toLowerCase(), SUB_COMMANDS0, new ArrayList<>());
        }
        return Collections.emptyList();
    }
}
