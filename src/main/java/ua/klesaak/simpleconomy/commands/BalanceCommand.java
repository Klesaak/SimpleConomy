package ua.klesaak.simpleconomy.commands;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.utils.AbstractBukkitCommand;

public class BalanceCommand extends AbstractBukkitCommand {
    private final SimpleEconomyManager manager;

    public BalanceCommand(SimpleEconomyManager manager) {
        this.manager = manager;
        this.manager.getPlugin().getCommand("balance").setExecutor(this);
    }

    @Override
    public void onReceiveCommand(CommandSender sender, Command command, String[] args) {
        Player playerSender = this.cmdVerifyPlayer(sender);
        if (args.length == 0) {
            val pd = manager.getStorage().getPlayer(playerSender.getName().toLowerCase());
            manager.getMessagesFile().sendBalanceInfo(playerSender, manager.getConfigFile().format(pd.getMoney()), String.valueOf(pd.getCoins())); //todo NumberUtils
            return;
        }
        if (args.length == 1 && sender.hasPermission("simpleconomy.others")) {
            String name = args[0];
            if (Bukkit.getPlayerExact(name) != null) {
                val otherPD = manager.getStorage().getPlayer(name.toLowerCase());
                manager.getMessagesFile().sendBalanceInfoOther(playerSender, name, manager.getConfigFile().format(otherPD.getMoney()), String.valueOf(otherPD.getCoins()));
                return;
            }
            manager.getMessagesFile().sendPlayerNotFound(playerSender);
        }
    }
}
