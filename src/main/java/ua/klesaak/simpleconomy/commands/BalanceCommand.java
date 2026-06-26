package ua.klesaak.simpleconomy.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;

import java.util.Collections;
import java.util.List;

import static ua.klesaak.simpleconomy.configurations.MessagesFile.*;

public class BalanceCommand extends AbstractBukkitCommand {
    private final SimpleEconomyManager manager;

    public BalanceCommand(SimpleEconomyManager manager) {
        super(manager.getPlugin(), "balance");
        this.manager = manager;
    }

    @Override
    public void onReceiveCommand(CommandSender sender, String label, String[] args) {
        var configFile = manager.getConfigFile();
        var messagesFile = manager.getMessagesFile();
        var storage = manager.getStorage();
        if (args.length == 0) {
            Player playerSender = this.cmdVerifyPlayer(sender);
            var senderName = playerSender.getName();
            messagesFile.getBalanceInfo()
                    .tag(BALANCE_PATTERN, configFile.formatMoney(storage.getMoneyBalance(senderName)))
                    .tag(COINS_PATTERN, configFile.formatCoins(storage.getCoinsBalance(senderName))).send(sender);
            return;
        }
        if (args.length == 1 && sender.hasPermission("simpleconomy.others")) {
            String otherName = args[0];
            if (storage.hasAccount(otherName)) {
                messagesFile.getBalanceInfoOther()
                        .tag(BALANCE_PATTERN, configFile.formatMoney(storage.getMoneyBalance(otherName)))
                        .tag(COINS_PATTERN, configFile.formatCoins(storage.getCoinsBalance(otherName)))
                        .tag(PLAYER_PATTERN, otherName).send(sender);
                return;
            }
            messagesFile.getPlayerNotFound().send(sender);
        }
    }

    @Override
    public List<String> onTabSuggest(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
