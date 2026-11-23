package net.klesaak.simpleconomy.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.klesaak.simpleconomy.configurations.ConfigFile;
import net.klesaak.simpleconomy.configurations.MessagesFile;
import net.klesaak.simpleconomy.manager.SimpleEconomyManager;

import java.util.Collections;
import java.util.List;

import static net.klesaak.simpleconomy.configurations.MessagesFile.*;

public class BalanceCommand extends AbstractBukkitCommand {
    private final SimpleEconomyManager manager;

    public BalanceCommand(SimpleEconomyManager manager) {
        super(manager.getPlugin(), "balance");
        this.manager = manager;
    }

    @Override
    public void onReceiveCommand(CommandSender sender, String label, String[] args) {
        var configFile = ConfigFile.get();
        var messagesFile = MessagesFile.get();
        var storage = manager.getStorage();
        Player playerSender = this.cmdVerifyPlayer(sender);
        var senderName = playerSender.getName();
        messagesFile.getBalanceInfo()
                .tag(BALANCE_PATTERN, configFile.formatMoney(storage.getMoneyBalance(senderName)))
                .tag(COINS_PATTERN, configFile.formatCoins(storage.getCoinsBalance(senderName))).send(sender);
    }

    @Override
    public List<String> onTabSuggest(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
