package ua.klesaak.simpleconomy.commands;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.utils.AbstractBukkitCommand;

import java.util.Objects;

import static ua.klesaak.simpleconomy.configurations.MessagesFile.*;

public class BalanceCommand extends AbstractBukkitCommand {
    private final SimpleEconomyManager manager;

    public BalanceCommand(SimpleEconomyManager manager) {
        this.manager = manager;
        Objects.requireNonNull(this.manager.getPlugin().getCommand("balance")).setExecutor(this);
    }

    @Override
    public void onReceiveCommand(CommandSender sender, String label, String[] args) {
        Player playerSender = this.cmdVerifyPlayer(sender);
        val configFile = manager.getConfigFile();
        val messagesFile = manager.getMessagesFile();
        if (args.length == 0) {
            val pd = manager.getStorage().getPlayer(playerSender.getName().toLowerCase());
            messagesFile.getBalanceInfo()
                    .tag(BALANCE_PATTERN, configFile.formatMoney(pd.getMoney()))
                    .tag(COINS_PATTERN, configFile.formatCoins(pd.getCoins())).send(sender);
            return;
        }
        if (args.length == 1 && sender.hasPermission("simpleconomy.others")) {
            String name = args[0];
            if (Bukkit.getPlayerExact(name) != null) {
                val otherPD = manager.getStorage().getPlayer(name.toLowerCase());
                messagesFile.getBalanceInfoOther()
                        .tag(BALANCE_PATTERN, configFile.formatMoney(otherPD.getMoney()))
                        .tag(COINS_PATTERN, configFile.formatCoins(otherPD.getCoins()))
                        .tag(PLAYER_PATTERN, name).send(sender);
                return;
            }
            messagesFile.getPlayerNotFound().send(playerSender);
        }
    }
}
