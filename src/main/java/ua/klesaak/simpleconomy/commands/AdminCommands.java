package ua.klesaak.simpleconomy.commands;

import lombok.NonNull;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;
import ua.klesaak.simpleconomy.utils.AbstractBukkitCommand;
import ua.klesaak.simpleconomy.utils.UtilityMethods;

import java.util.*;

import static ua.klesaak.simpleconomy.configurations.MessagesFile.*;

public class AdminCommands extends AbstractBukkitCommand implements TabCompleter {
    private static final List<String> SUB_COMMANDS0 = Arrays.asList("reload", "addmoney", "addcoins", "wmoney", "wcoins", "setmoney", "setcoins", "delacc");
    private final SimpleEconomyManager manager;

    public AdminCommands(SimpleEconomyManager manager) {
        this.manager = manager;
        Objects.requireNonNull(this.manager.getPlugin().getCommand("sconomy")).setExecutor(this);
        Objects.requireNonNull(this.manager.getPlugin().getCommand("sconomy")).setTabCompleter(this);
    }


    @Override
    public void onReceiveCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + manager.getPlugin().getDescription().getName() + " v" + manager.getPlugin().getDescription().getVersion() + ChatColor.RED + " by Klesaak");
            sender.sendMessage("");
            sender.sendMessage("§6/" + label + " reload - перезагрузить конфиги.");
            sender.sendMessage("§6/" + label + " addmoney <ник> <сумма> - выдать деньги.");
            sender.sendMessage("§6/" + label + " addcoins <ник> <сумма> - выдать коины.");
            sender.sendMessage("§6/" + label + " wmoney <ник> <сумма> - забрать деньги.");
            sender.sendMessage("§6/" + label + " wcoins <ник> <сумма> - забрать коины.");
            sender.sendMessage("§6/" + label + " setmoney <ник> <сумма> - установить деньги.");
            sender.sendMessage("§6/" + label + " setcoins <ник> <сумма> - установить коины.");
            sender.sendMessage("§6/" + label + " delacc <ник> - удалить аккаунт.");
            return;
        }
        val storage = this.manager.getStorage();
        val config = this.manager.getConfigFile();
        val messagesFile = this.manager.getMessagesFile();
        switch (args[0].toLowerCase()) {
            case "reload": {
                sender.sendMessage(this.manager.reload());
                break;
            }
            case "addmoney": {
                this.cmdVerify(args.length != 3, "§6/" + label + " addmoney <ник> <сумма> - выдать деньги.");
                val nickName = args[1].toLowerCase();
                val money = this.cmdVerifyInt(args[2]);
                if (storage.getPlayer(nickName).getMoney() + money > config.getMaxBalance()) {
                    sender.sendMessage("§cБаланс игрока превысит максимально допустимый.");
                    return;
                }
                storage.depositMoney(nickName, money);
                messagesFile.getVaultAddMoney().tag(PLAYER_PATTERN, nickName).tag(MONEY_PATTERN, config.formatMoney(money)).send(sender);
                break;
            }
            case "addcoins": {
                this.cmdVerify(args.length != 3, "§6/" + label + " addcoins <ник> <сумма> - выдать коины.");
                val nickName = args[1].toLowerCase();
                val coins = this.cmdVerifyInt(args[2]);
                if (storage.getPlayer(nickName).getCoins() + coins > config.getMaxCoins()) {
                    sender.sendMessage("§cБаланс коинов превысит максимально допустимый.");
                    return;
                }
                storage.depositCoins(nickName, coins);
                messagesFile.getCoinsAddMoney().tag(PLAYER_PATTERN, nickName).tag(COINS_PATTERN, config.formatCoins(coins)).send(sender);
                break;
            }
            case "setmoney": {
                this.cmdVerify(args.length != 3, "§6/" + label + " setmoney <ник> <сумма> - установить деньги.");
                val nickName = args[1].toLowerCase();
                val money = this.cmdVerifyInt(args[2]);
                if (money > config.getMaxBalance()) {
                    sender.sendMessage("§cБаланс игрока превысит максимально допустимый.");
                    return;
                }
                storage.setMoney(nickName, money);
                messagesFile.getVaultSetMoney().tag(PLAYER_PATTERN, nickName).tag(MONEY_PATTERN, config.formatMoney(money)).send(sender);
                break;
            }
            case "setcoins": {
                this.cmdVerify(args.length != 3,"§6/" + label + " setcoins <ник> <сумма> - установить коины.");
                val nickName = args[1].toLowerCase();
                val coins = this.cmdVerifyInt(args[2]);
                if (coins > config.getMaxCoins()) {
                    sender.sendMessage("§cБаланс игрока превысит максимально допустимый.");
                    return;
                }
                storage.setCoins(nickName, coins);
                messagesFile.getCoinsSetMoney().tag(PLAYER_PATTERN, nickName).tag(COINS_PATTERN, config.formatCoins(coins)).send(sender);
                break;
            }
            case "wmoney": {
                this.cmdVerify(args.length != 3, "§6/" + label + " wmoney <ник> <сумма> - забрать деньги.");
                val nickName = args[1].toLowerCase();
                val money = this.cmdVerifyInt(args[2]);
                if (storage.getPlayer(nickName).getMoney() - money < 0) {
                    sender.sendMessage("§cБаланс игрока не может быть меньше ноля.");
                    return;
                }
                storage.withdrawMoney(nickName, money);
                messagesFile.getVaultSenderWithdrawn().tag(PLAYER_PATTERN, nickName).tag(MONEY_PATTERN, money).send(sender);
                break;
            }
            case "wcoins": {
                this.cmdVerify(args.length != 3,"§6/" + label + " wcoins <ник> <сумма> - забрать коины.");
                val nickName = args[1].toLowerCase();
                val coins = this.cmdVerifyInt(args[2]);
                if (storage.getPlayer(nickName).getCoins() - coins < 0) {
                    sender.sendMessage("§cБаланс коинов игрока не может быть меньше ноля.");
                    return;
                }
                storage.withdrawCoins(nickName, coins);
                messagesFile.getCoinsSenderWithdrawn().tag(PLAYER_PATTERN, nickName).tag(COINS_PATTERN, config.formatCoins(coins)).send(sender);
                break;
            }
            case "delacc": {
                this.cmdVerify(args.length != 2, "§6/" + label + " delacc <ник> - удалить аккаунт.");
                val nickName = args[1].toLowerCase();
                storage.deleteAccount(nickName);
                sender.sendMessage("§6Балансы игрока " + nickName + " успешно сброшены.");
                break;
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length == 1) {
            return UtilityMethods.copyPartialMatches(args[0].toLowerCase(), SUB_COMMANDS0, new ArrayList<>());
        }
        return Collections.emptyList();
    }
}
