package ua.klesaak.simpleconomy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.utils.AbstractBukkitCommand;

public class PayCommand extends AbstractBukkitCommand {
    private final SimpleEconomyManager manager;

    public PayCommand(SimpleEconomyManager manager) {
        this.manager = manager;
        this.manager.getPlugin().getCommand("pay").setExecutor(this);
    }

    @Override
    public void onReceiveCommand(CommandSender sender, Command command, String[] args) {
        Player playerSender = this.cmdVerifyPlayer(sender);


    }
}
