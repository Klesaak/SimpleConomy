package ua.klesaak.simpleconomy.commands;

import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.utils.AbstractBukkitCommand;

import java.util.List;

public class BalTopCommand extends AbstractBukkitCommand implements TabCompleter {
    private final SimpleEconomyManager manager;

    public BalTopCommand(SimpleEconomyManager manager) {
        this.manager = manager;
        this.manager.getPlugin().getCommand("baltop").setExecutor(this);
        this.manager.getPlugin().getCommand("baltop").setTabCompleter(this);
    }


    @Override
    public void onReceiveCommand(CommandSender sender, Command command, String[] args) {

    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String label, String[] strings) {
        return null;
    }
}
