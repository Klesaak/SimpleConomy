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

public class BalTopCommand extends AbstractBukkitCommand implements TabCompleter {
    private static final List<String> SUB_COMMANDS0 = Arrays.asList("money", "coins");
    private final SimpleEconomyManager manager;

    public BalTopCommand(SimpleEconomyManager manager) {
        this.manager = manager;
        Objects.requireNonNull(this.manager.getPlugin().getCommand("baltop")).setExecutor(this);
        Objects.requireNonNull(this.manager.getPlugin().getCommand("baltop")).setTabCompleter(this);
    }


    @Override
    public void onReceiveCommand(CommandSender sender, String label, String[] args) {
        val messagesFile = this.manager.getMessagesFile();
        val topManager = this.manager.getTopManager();
        if (args.length == 0) {
            messagesFile.sendMoneyTop(sender, topManager.getMoneyTop());
            return;
        }
        switch (args[0]) {
            case "money": {
                messagesFile.sendMoneyTop(sender, topManager.getMoneyTop());
                break;
            }
            case "coins": {
                messagesFile.sendCoinsTop(sender, topManager.getCoinsTop());
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
