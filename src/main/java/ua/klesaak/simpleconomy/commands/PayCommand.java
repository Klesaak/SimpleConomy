package ua.klesaak.simpleconomy.commands;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.utils.AbstractBukkitCommand;

import java.util.Objects;

public class PayCommand extends AbstractBukkitCommand {
    private final SimpleEconomyManager manager;

    public PayCommand(SimpleEconomyManager manager) {
        this.manager = manager;
        Objects.requireNonNull(this.manager.getPlugin().getCommand("pay")).setExecutor(this);
    }

    @Override
    public void onReceiveCommand(CommandSender sender, String label, String[] args) {
        Player playerSender = this.cmdVerifyPlayer(sender);
        val messagesFile = this.manager.getMessagesFile();
        if (args.length != 2) {
            messagesFile.sendVaultPayUsage(sender, label);
            return;
        }
        val config = this.manager.getConfigFile();
        val storage = this.manager.getStorage();
        val playerName = args[0];
        int sum = 0;
        try {
            sum = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            messagesFile.sendNotInteger(sender, args[1]);
        }
        if (playerSender.getName().equalsIgnoreCase(playerName)) {
            messagesFile.sendPaySelf(sender);
            return;
        }
        val receiver = Bukkit.getPlayerExact(playerName);
        if (receiver == null) {
            messagesFile.sendPlayerNotFound(sender);
            return;
        }
        val receiverBalance = storage.getPlayer(playerName.toLowerCase()).getMoney();
        if (!storage.hasMoney(playerSender.getName().toLowerCase(), sum)) {
            messagesFile.sendVaultNoMoney(sender, config.formatMoney(storage.getPlayer(playerSender.getName().toLowerCase()).getMoney()));
            return;
        }
        if (config.getMinTransactionSum() > sum) {
            messagesFile.sendErrorMinTransaction(sender, String.valueOf(config.getMinTransactionSum()), String.valueOf(sum));
            return;
        }
        if (receiverBalance + sum > config.getMaxBalance()) {
            messagesFile.sendVaultPayErrorMaxBalance(sender, config.formatMoney(receiverBalance), config.formatMoney(config.getMaxBalance()));
            return;
        }
        storage.depositMoney(playerName.toLowerCase(), sum);
        storage.withdrawMoney(playerSender.getName().toLowerCase(), sum);
        val receiverNewBalance = config.formatMoney(storage.getPlayer(playerName.toLowerCase()).getMoney());
        messagesFile.sendVaultPaySuccessful(sender, playerName, config.formatMoney(sum), config.formatMoney(storage.getPlayer(playerSender.getName().toLowerCase()).getMoney()));
        messagesFile.sendVaultPayReceived(receiver, playerSender.getName(), config.formatMoney(sum), receiverNewBalance);
    }
}
