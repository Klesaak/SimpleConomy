package ua.klesaak.simpleconomy.commands;

import lombok.NonNull;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.utils.AbstractBukkitCommand;
import ua.klesaak.simpleconomy.utils.UtilityMethods;

import java.util.*;

public class BalTopCommand extends AbstractBukkitCommand {
    private static final List<String> SUB_COMMANDS0 = Arrays.asList("money", "coins");
    private final SimpleEconomyManager manager;

    public BalTopCommand(SimpleEconomyManager manager) {
        super(manager.getPlugin(), "baltop");
        this.manager = manager;
    }


    @Override
    public void onReceiveCommand(CommandSender sender, String label, String[] args) {
        var topManager = this.manager.getTopManager();
        if (args.length == 0) {
            sender.sendMessage(topManager.getMoneyTop());
            return;
        }
        switch (args[0]) {
            case "money": {
                sender.sendMessage(topManager.getMoneyTop());
                break;
            }
            case "coins": {
                sender.sendMessage(topManager.getCoinsTop());
                break;
            }
        }
    }

    @Override
    public List<String> onTabSuggest(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return UtilityMethods.copyPartialMatches(args[0].toLowerCase(), SUB_COMMANDS0, new ArrayList<>());
        }
        return Collections.emptyList();
    }
}
