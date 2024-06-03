package ua.klesaak.simpleconomy.commands;

import lombok.val;
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
        val configFile = manager.getConfigFile();
        val messagesFile = manager.getMessagesFile();
        val storage = manager.getStorage();
        if (args.length == 0) {
            Player playerSender = this.cmdVerifyPlayer(sender);
            val senderNameLC = playerSender.getName().toLowerCase();
            messagesFile.getBalanceInfo()
                    .tag(BALANCE_PATTERN, configFile.formatMoney(storage.getMoneyBalance(senderNameLC)))
                    .tag(COINS_PATTERN, configFile.formatCoins(storage.getCoinsBalance(senderNameLC))).send(sender);
            return;
        }
        if (args.length == 1 && sender.hasPermission("simpleconomy.others")) {
            String otherName = args[0];
            String otherNameLC = otherName.toLowerCase();
            if (storage.hasAccount(otherNameLC)) {
                messagesFile.getBalanceInfoOther()
                        .tag(BALANCE_PATTERN, configFile.formatMoney(storage.getMoneyBalance(otherNameLC)))
                        .tag(COINS_PATTERN, configFile.formatCoins(storage.getCoinsBalance(otherNameLC)))
                        .tag(PLAYER_PATTERN, otherName).send(sender);
                return;
            }
            messagesFile.getPlayerNotFound().send(sender);
        }
    }
}
