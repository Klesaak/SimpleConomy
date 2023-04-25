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

public class BalTopCommand extends AbstractBukkitCommand implements TabCompleter {
    private static final List<String> SUB_COMMANDS0 = Arrays.asList("money", "coins");
    private final SimpleEconomyManager manager;

    public BalTopCommand(SimpleEconomyManager manager) {
        this.manager = manager;
        this.manager.getPlugin().getCommand("baltop").setExecutor(this);
        this.manager.getPlugin().getCommand("baltop").setTabCompleter(this);
    }


    @Override
    public void onReceiveCommand(CommandSender sender, Command command, String[] args) {
        if (args.length == 0) {
            this.manager.getMessagesFile().sendMoneyTop(sender, this.manager.getTopManager().getMoneyTop());
            return;
        }
        switch (args[0]) {
            case "money": {
                this.manager.getMessagesFile().sendMoneyTop(sender, this.manager.getTopManager().getMoneyTop());
                break;
            }
            case "coins": {
                this.manager.getMessagesFile().sendCoinsTop(sender, this.manager.getTopManager().getCoinsTop());
                break;
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length == 1) {
            return UtilityMethods.copyPartialMatches(args[0].toLowerCase(), SUB_COMMANDS0, new ArrayList<>());
        }
        return Collections.emptyList();
    }
}
