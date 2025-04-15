package ua.klesaak.simpleconomy.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.utils.AbstractBukkitCommand;

import java.util.Collections;
import java.util.List;

import static ua.klesaak.simpleconomy.configurations.MessagesFile.*;

public class PayCommand extends AbstractBukkitCommand {
    private final SimpleEconomyManager manager;

    public PayCommand(SimpleEconomyManager manager) {
        super(manager.getPlugin(), "pay");
        this.manager = manager;
    }

    @Override
    public void onReceiveCommand(CommandSender sender, String label, String[] args) {
        Player playerSender = this.cmdVerifyPlayer(sender);
        String senderNameLC = playerSender.getName().toLowerCase();
        var messagesFile = this.manager.getMessagesFile();
        if (args.length != 2) {
            messagesFile.getVaultPayUsage().tag(LABEL_PATTERN, label).send(sender);
            return;
        }
        var config = this.manager.getConfigFile();
        var storage = this.manager.getStorage();
        var senderBalance = storage.getMoneyBalance(senderNameLC);
        var playerName = args[0];
        var playerNameLC = playerName.toLowerCase();
        int sum = this.cmdVerifyInt(args[1], messagesFile.getNotInteger().tag(NUMBER_PATTERN, args[1]));
        if (playerSender.getName().equalsIgnoreCase(playerName)) {
            messagesFile.getPaySelf().send(sender);
            return;
        }
        var receiver = Bukkit.getPlayerExact(playerName);
        if (receiver == null) {
            messagesFile.getPlayerNotFound().send(sender);
            return;
        }
        var receiverBalance = storage.getMoneyBalance(playerNameLC);
        if (senderBalance < sum) {
            messagesFile.getVaultNoMoney().tag(BALANCE_PATTERN, config.formatMoney(senderBalance)).send(sender);
            return;
        }
        if (config.getMinTransactionSum() > sum) {
            messagesFile.getErrorMinTransaction()
                    .tag(SUM_PATTERN, config.formatMoney(config.getMinTransactionSum()))
                    .tag(MONEY_PATTERN, config.formatMoney(sum)).send(sender);
            return;
        }
        if (receiverBalance + sum > config.getMaxBalance()) {
            messagesFile.getVaultPayErrorMaxBalance()
                    .tag(BALANCE_PATTERN, config.formatMoney(receiverBalance))
                    .tag(MAX_BALANCE_PATTERN, config.formatMoney(config.getMaxBalance())).send(sender);
            return;
        }
        storage.depositMoney(playerNameLC, sum);
        storage.withdrawMoney(senderNameLC, sum);
        var receiverNewBalance = config.formatMoney(sum + receiverBalance);
        var newSenderBalance = senderBalance - sum;
        messagesFile.getVaultPaySuccessful()
                .tag(PLAYER_PATTERN, playerName)
                .tag(MONEY_PATTERN, config.formatMoney(sum))
                .tag(NEW_BALANCE_PATTERN, config.formatMoney(newSenderBalance)).send(sender);
        messagesFile.getVaultPayReceived()
                .tag(PLAYER_PATTERN, playerSender.getName())
                .tag(MONEY_PATTERN, config.formatMoney(sum))
                .tag(NEW_BALANCE_PATTERN, receiverNewBalance).send(receiver);
    }

    @Override
    public List<String> onTabSuggest(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
