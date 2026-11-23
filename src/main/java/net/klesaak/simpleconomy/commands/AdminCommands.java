package net.klesaak.simpleconomy.commands;

import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import net.klesaak.simpleconomy.api.SimpleEconomyAPI;
import net.klesaak.simpleconomy.configurations.ConfigFile;
import net.klesaak.simpleconomy.configurations.MessagesFile;
import net.klesaak.simpleconomy.manager.SimpleEconomyManager;
import net.klesaak.simpleconomy.utils.UtilityMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.klesaak.simpleconomy.configurations.MessagesFile.*;

public class AdminCommands extends AbstractBukkitCommand {
    private static final List<String> SUB_COMMANDS0 = Arrays.asList("reload", "addmoney", "addcoins", "wmoney", "wcoins", "setmoney", "setcoins", "clear");
    private final SimpleEconomyManager manager;

    public AdminCommands(SimpleEconomyManager manager) {
        super(manager.getPlugin(), "sconomy");
        this.manager = manager;
    }


    @Override
    public void onReceiveCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + manager.getPlugin().getDescription().getName() + " v" + manager.getPlugin().getDescription().getVersion() + ChatColor.RED + " by Klesaak");
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " reload - перезагрузить конфиги.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " addmoney <ник> <сумма> - выдать деньги.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " addcoins <ник> <сумма> - выдать коины.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " wmoney <ник> <сумма> - забрать деньги.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " wcoins <ник> <сумма> - забрать коины.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " setmoney <ник> <сумма> - установить деньги.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " setcoins <ник> <сумма> - установить коины.");
            sender.sendMessage(ChatColor.GOLD + "/" + label + " clear <ник> - очистить балансы игроку.");
            return;
        }
        var storage = this.manager.getStorage();
        var config = ConfigFile.get();
        var messagesFile = MessagesFile.get();
        SimpleEconomyAPI simpleEconomyAPI = SimpleEconomyAPI.get();
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                this.manager.reload();
                sender.sendMessage(ChatColor.GREEN + this.manager.getPlugin().getDescription().getName() + " reloaded!");
            }
            case "addmoney" -> {
                this.cmdVerify(args.length != 3,"<gold>/" + label + " addmoney <ник> <сумма> - выдать деньги.");
                var nickName = args[1];
                var money = this.cmdVerifyInt(args[2]);
                simpleEconomyAPI.getMoneyBalanceAsync(nickName).thenAccept(playerBalance -> {
                    if (playerBalance + money > config.getMaxBalance()) {
                        sender.sendMessage(ChatColor.RED + "Баланс игрока превысит максимально допустимый.");
                        return;
                    }
                    simpleEconomyAPI.depositMoneyAsync(nickName, money, null);
                    messagesFile.getVaultAddMoney()
                            .tag(PLAYER_PATTERN, nickName)
                            .tag(MONEY_PATTERN, config.formatMoney(money)).send(sender);
                });
            }
            case "addcoins" -> {
                this.cmdVerify(args.length != 3, "<gold>/" + label + " addcoins <ник> <сумма> - выдать коины.");
                var nickName = args[1];
                var coins = this.cmdVerifyInt(args[2]);
                simpleEconomyAPI.getCoinsBalanceAsync(nickName).thenAccept(playerBalance -> {
                    if (playerBalance + coins > config.getMaxCoins()) {
                        sender.sendMessage(ChatColor.RED + "Баланс коинов превысит максимально допустимый.");
                        return;
                    }
                    simpleEconomyAPI.depositCoinsAsync(nickName, coins, null);
                    messagesFile.getCoinsAddMoney()
                            .tag(PLAYER_PATTERN, nickName)
                            .tag(COINS_PATTERN, config.formatCoins(coins)).send(sender);
                });
            }
            case "setmoney" -> {
                this.cmdVerify(args.length != 3, "<gold>/" + label + " setmoney <ник> <сумма> - установить деньги.");
                var nickName = args[1];
                var money = this.cmdVerifyInt(args[2]);
                if (money > config.getMaxBalance()) {
                    sender.sendMessage(ChatColor.RED + "Баланс игрока превысит максимально допустимый.");
                    return;
                }
                if (money < 0) {
                    sender.sendMessage(ChatColor.RED + "Баланс игрока не может быть меньше нуля.");
                    return;
                }
                simpleEconomyAPI.setMoneyAsync(nickName, money, null);
                messagesFile.getVaultSetMoney()
                        .tag(PLAYER_PATTERN, nickName)
                        .tag(MONEY_PATTERN, config.formatMoney(money)).send(sender);
            }
            case "setcoins" -> {
                this.cmdVerify(args.length != 3,"<gold>/" + label + " setcoins <ник> <сумма> - установить коины.");
                var nickName = args[1];
                var coins = this.cmdVerifyInt(args[2]);
                if (coins > config.getMaxCoins()) {
                    sender.sendMessage(ChatColor.RED + "Баланс игрока превысит максимально допустимый.");
                    return;
                }
                if (coins < 0) {
                    sender.sendMessage(ChatColor.RED + "Баланс коинов игрока не может быть меньше нуля.");
                    return;
                }
                simpleEconomyAPI.setCoinsAsync(nickName, coins, null);
                messagesFile.getCoinsSetMoney()
                        .tag(PLAYER_PATTERN, nickName)
                        .tag(COINS_PATTERN, config.formatCoins(coins)).send(sender);
            }
            case "wmoney" -> {
                this.cmdVerify(args.length != 3,"<gold>/" + label + " wmoney <ник> <сумма> - забрать деньги.");
                var nickName = args[1];
                var money = this.cmdVerifyInt(args[2]);
                simpleEconomyAPI.getMoneyBalanceAsync(nickName).thenAccept(playerBalance -> {
                    if (playerBalance - money < 0) {
                        sender.sendMessage(ChatColor.RED + "Баланс игрока не может быть меньше нуля.");
                        return;
                    }
                    simpleEconomyAPI.withdrawMoneyAsync(nickName, money, null);
                    messagesFile.getVaultSenderWithdrawn()
                            .tag(PLAYER_PATTERN, nickName)
                            .tag(MONEY_PATTERN, money).send(sender);
                });
            }
            case "wcoins" -> {
                this.cmdVerify(args.length != 3,"<gold>/" + label + " wcoins <ник> <сумма> - забрать коины.");
                var nickName = args[1];
                var coins = this.cmdVerifyInt(args[2]);
                simpleEconomyAPI.getCoinsBalanceAsync(nickName).thenAccept(playerBalance -> {
                    if (playerBalance - coins < 0) {
                        sender.sendMessage(ChatColor.RED + "Баланс коинов игрока не может быть меньше нуля.");
                        return;
                    }
                    simpleEconomyAPI.withdrawCoinsAsync(nickName, coins, null);
                    messagesFile.getCoinsSenderWithdrawn()
                            .tag(PLAYER_PATTERN, nickName)
                            .tag(COINS_PATTERN, config.formatCoins(coins)).send(sender);
                });
            }
            case "clear" -> {
                this.cmdVerify(args.length != 2, "<gold>/" + label + " clear <ник> - очистить балансы игроку.");
                var nickName = args[1];
                storage.clearBalances(nickName);
                sender.sendMessage(ChatColor.GOLD + "Балансы игрока " + ChatColor.RED + nickName + ChatColor.GOLD +  " успешно сброшены.");
            }
        }
    }

    @Override
    public List<String> onTabSuggest(@NonNull CommandSender sender, String[] args) {
        if (args.length == 1) {
            return UtilityMethods.copyPartialMatches(args[0].toLowerCase(), SUB_COMMANDS0, new ArrayList<>());
        }
        return Collections.emptyList();
    }
}
