package ua.klesaak.simpleconomy.commands;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.utils.AbstractBukkitCommand;

import java.util.Objects;

import static ua.klesaak.simpleconomy.configurations.MessagesFile.*;

public class PayCommand extends AbstractBukkitCommand {
    private final SimpleEconomyManager manager;

    public PayCommand(SimpleEconomyManager manager) {
        this.manager = manager;
        Objects.requireNonNull(this.manager.getPlugin().getCommand("pay")).setExecutor(this);
    }

    @Override
    public void onReceiveCommand(CommandSender sender, String label, String[] args) {
        Player playerSender = this.cmdVerifyPlayer(sender);
        String senderLC = playerSender.getName().toLowerCase();
        val messagesFile = this.manager.getMessagesFile();
        if (args.length != 2) {
            messagesFile.getVaultPayUsage().tag(LABEL_PATTERN, label).send(sender);
            return;
        }
        val config = this.manager.getConfigFile();
        val storage = this.manager.getStorage();
        val senderData = storage.getPlayer(senderLC);
        val playerName = args[0];
        val playerNameLC = playerName.toLowerCase();
        int sum = 0;
        try {
            sum = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            messagesFile.getNotInteger().tag(NUMBER_PATTERN, args[1]).send(sender);
        }
        if (playerSender.getName().equalsIgnoreCase(playerName)) {
            messagesFile.getPaySelf().send(sender);
            return;
        }
        val receiver = Bukkit.getPlayerExact(playerName);
        if (receiver == null) {
            messagesFile.getPlayerNotFound().send(sender);
            return;
        }
        val receiverBalance = storage.getPlayer(playerNameLC).getMoney();
        if (senderData.getMoney() < sum) {
            messagesFile.getVaultNoMoney().tag(BALANCE_PATTERN, config.formatMoney(senderData.getMoney())).send(sender);
            return;
        }
        if (config.getMinTransactionSum() > sum) {
            messagesFile.getErrorMinTransaction().tag(SUM_PATTERN, config.getMinTransactionSum()).tag(MONEY_PATTERN, sum).send(sender);
            return;
        }
        if (receiverBalance + sum > config.getMaxBalance()) {
            messagesFile.getVaultPayErrorMaxBalance()
                    .tag(BALANCE_PATTERN, config.formatMoney(receiverBalance))
                    .tag(MAX_BALANCE_PATTERN, config.formatMoney(config.getMaxBalance())).send(sender);
            return;
        }
        storage.depositMoney(playerNameLC, sum);
        storage.withdrawMoney(senderLC, sum);
        val receiverNewBalance = config.formatMoney(sum + receiverBalance);
        messagesFile.getVaultPaySuccessful()
                .tag(PLAYER_PATTERN, playerName)
                .tag(MONEY_PATTERN, config.formatMoney(sum))
                .tag(NEW_BALANCE_PATTERN, config.formatMoney(senderData.getMoney())).send(sender);
        messagesFile.getVaultPayReceived()
                .tag(PLAYER_PATTERN, playerSender.getName())
                .tag(MONEY_PATTERN, config.formatMoney(sum))
                .tag(NEW_BALANCE_PATTERN, receiverNewBalance).send(receiver);
    }
}
