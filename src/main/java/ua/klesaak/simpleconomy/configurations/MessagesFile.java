package ua.klesaak.simpleconomy.configurations;

import com.google.common.base.Joiner;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import ua.klesaak.simpleconomy.utils.UtilityMethods;

import java.util.Collection;
import java.util.regex.Pattern;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessagesFile extends PluginConfig {
    private static final String BALANCE = "(balance)";
    private static final String NEW_BALANCE = "(new-balance)";
    private static final String MAX_BALANCE = "(max-balance)";
    private static final String LABEL = "(label)";
    private static final String COINS = "(coins)";
    private static final String PLAYER = "(player)";
    private static final String MONEY = "(money)";
    private static final String SUM = "(sum)";
    private static final String TOP = "(top)";
    private static final String INDEX = "(index)";
    private static final String NUMBER = "(number)";

    public static final Pattern BALANCE_PATTERN = Pattern.compile(BALANCE, Pattern.LITERAL);
    public static final Pattern NEW_BALANCE_PATTERN = Pattern.compile(NEW_BALANCE, Pattern.LITERAL);
    public static final Pattern MAX_BALANCE_PATTERN = Pattern.compile(MAX_BALANCE, Pattern.LITERAL);
    public static final Pattern LABEL_PATTERN = Pattern.compile(LABEL, Pattern.LITERAL);
    public static final Pattern COINS_PATTERN = Pattern.compile(COINS, Pattern.LITERAL);
    public static final Pattern PLAYER_PATTERN = Pattern.compile(PLAYER, Pattern.LITERAL);
    public static final Pattern MONEY_PATTERN = Pattern.compile(MONEY, Pattern.LITERAL);
    public static final Pattern SUM_PATTERN = Pattern.compile(SUM, Pattern.LITERAL);
    public static final Pattern TOP_PATTERN = Pattern.compile(TOP, Pattern.LITERAL);
    public static final Pattern INDEX_PATTERN = Pattern.compile(INDEX, Pattern.LITERAL);
    public static final Pattern NUMBER_PATTERN = Pattern.compile(NUMBER, Pattern.LITERAL);

    String balanceInfo, balanceInfoOther, vaultPaySuccessful, vaultPayErrorMaxBalance, vaultNoMoney, vaultPayReceived, errorMinTransaction, vaultNoPlayerMoney, vaultPayUsage;
    String vaultSenderWithdrawn, playerNotFound, notInteger, paySelf, vaultAddMoney, vaultSetMoney;
    String coinsNoPlayerMoney, coinsSenderWithdrawn, coinsAddMoney, coinsSetMoney;


    public MessagesFile(JavaPlugin plugin) {
        super(plugin, "messages.yml");
        this.balanceInfo = this.transformList("balanceInfo");
        this.balanceInfoOther = this.transformList("balanceInfoOther");
        this.vaultPaySuccessful = this.transformList("vaultPaySuccessful");
        this.vaultPayErrorMaxBalance = this.transformList("vaultPayErrorMaxBalance");
        this.vaultNoMoney = this.transformList("vaultNoMoney");
        this.vaultPayReceived = this.transformList("vaultPayReceived");
        this.errorMinTransaction = this.transformList("errorMinTransaction");
        this.vaultNoPlayerMoney = this.transformList("vaultNoPlayerMoney");
        this.vaultPayUsage = this.transformList("vaultPayUsage");
        this.vaultSenderWithdrawn = this.transformList("vaultSenderWithdrawn");
        this.playerNotFound = this.transformList("playerNotFound");
        this.notInteger = this.transformList("notInteger");
        this.paySelf = this.transformList("paySelf");
        this.vaultAddMoney = this.transformList("vaultAddMoney");
        this.vaultSetMoney = this.transformList("vaultSetMoney");
        this.coinsNoPlayerMoney = this.transformList("coinsNoPlayerMoney");
        this.coinsSenderWithdrawn = this.transformList("coinsSenderWithdrawn");
        this.coinsAddMoney = this.transformList("coinsAddMoney");
        this.coinsSetMoney = this.transformList("coinsSetMoney");
    }

    private String transformList(String key) {
        String message = "";
        if (this.isString(key)) {
            message = this.getString(key);
        } else if (this.isList(key)) {
            message = Joiner.on('\n').join(this.getStringList(key));
        }
        return UtilityMethods.color(message);
    }

    public void sendBalanceInfo(CommandSender sender, String balance, String coins) {
        String format = this.balanceInfo;
        format = UtilityMethods.replaceAll(BALANCE_PATTERN, format, ()-> balance);
        format = UtilityMethods.replaceAll(COINS_PATTERN, format, ()-> coins);
        sender.sendMessage(format);
    }

    public void sendBalanceInfoOther(CommandSender sender, String player, String balance, String coins) {
        String format = this.balanceInfoOther;
        format = UtilityMethods.replaceAll(BALANCE_PATTERN, format, ()-> balance);
        format = UtilityMethods.replaceAll(COINS_PATTERN, format, ()-> coins);
        format = UtilityMethods.replaceAll(PLAYER_PATTERN, format, ()-> player);
        sender.sendMessage(format);
    }

    public void sendVaultPaySuccessful(CommandSender sender, String player, String money, String newBalance) {
        String format = this.vaultPaySuccessful;
        format = UtilityMethods.replaceAll(PLAYER_PATTERN, format, ()-> player);
        format = UtilityMethods.replaceAll(MONEY_PATTERN, format, ()-> money);
        format = UtilityMethods.replaceAll(NEW_BALANCE_PATTERN, format, ()-> newBalance);
        sender.sendMessage(format);
    }

    public void sendVaultPayErrorMaxBalance(CommandSender sender, String balance, String maxBalance) {
        String format = this.vaultPayErrorMaxBalance;
        format = UtilityMethods.replaceAll(BALANCE_PATTERN, format, ()-> balance);
        format = UtilityMethods.replaceAll(MAX_BALANCE_PATTERN, format, ()-> maxBalance);
        sender.sendMessage(format);
    }

    public void sendVaultNoMoney(CommandSender sender, String balance) {
        String format = this.vaultNoMoney;
        format = UtilityMethods.replaceAll(BALANCE_PATTERN, format, ()-> balance);
        sender.sendMessage(format);
    }

    public void sendVaultPayReceived(CommandSender sender, String player, String money, String newBalance) {
        String format = this.vaultPayReceived;
        format = UtilityMethods.replaceAll(PLAYER_PATTERN, format, ()-> player);
        format = UtilityMethods.replaceAll(MONEY_PATTERN, format, ()-> money);
        format = UtilityMethods.replaceAll(NEW_BALANCE_PATTERN, format, ()-> newBalance);
        sender.sendMessage(format);
    }

    public void sendErrorMinTransaction(CommandSender sender, String minimumSum, String balance) {
        String format = this.errorMinTransaction;
        format = UtilityMethods.replaceAll(BALANCE_PATTERN, format, ()-> balance);
        format = UtilityMethods.replaceAll(SUM_PATTERN, format, ()-> minimumSum);
        sender.sendMessage(format);
    }

    public void sendVaultNoPlayerMoney(CommandSender sender) {
        sender.sendMessage(this.vaultNoPlayerMoney);
    }

    public void sendVaultPayUsage(CommandSender sender, String label) {
        String format = this.vaultPayUsage;
        format = UtilityMethods.replaceAll(LABEL_PATTERN, format, ()-> label);
        sender.sendMessage(format);
    }

    public void sendVaultSenderWithdrawn(CommandSender sender, String player, String money) {
        String format = this.vaultSenderWithdrawn;
        format = UtilityMethods.replaceAll(PLAYER_PATTERN, format, ()-> player);
        format = UtilityMethods.replaceAll(MONEY_PATTERN, format, ()-> money);
        sender.sendMessage(format);
    }

    public void sendPlayerNotFound(CommandSender sender) {
        sender.sendMessage(this.playerNotFound);
    }

    public void sendNotInteger(CommandSender sender, String number) {
        String format = this.notInteger;
        format = UtilityMethods.replaceAll(NUMBER_PATTERN, format, ()-> number);
        sender.sendMessage(format);
    }
    public void sendPaySelf(CommandSender sender) {
        sender.sendMessage(this.paySelf);
    }

    public void sendVaultAddMoney(CommandSender sender, String player, String money) {
        String format = this.vaultAddMoney;
        format = UtilityMethods.replaceAll(PLAYER_PATTERN, format, ()-> player);
        format = UtilityMethods.replaceAll(MONEY_PATTERN, format, ()-> money);
        sender.sendMessage(format);
    }

    public void sendVaultSetMoney(CommandSender sender, String player, String money) {
        String format = this.vaultSetMoney;
        format = UtilityMethods.replaceAll(PLAYER_PATTERN, format, ()-> player);
        format = UtilityMethods.replaceAll(MONEY_PATTERN, format, ()-> money);
        sender.sendMessage(format);
    }

    //coins messages
    public void sendCoinsNoPlayerMoney(CommandSender sender) {
        sender.sendMessage(this.coinsNoPlayerMoney);
    }

    public void sendCoinsSenderWithdrawn(CommandSender sender, String player, String coins) {
        String format = this.coinsSenderWithdrawn;
        format = UtilityMethods.replaceAll(PLAYER_PATTERN, format, ()-> player);
        format = UtilityMethods.replaceAll(COINS_PATTERN, format, ()-> coins);
        sender.sendMessage(format);
    }

    public void sendCoinsAddMoney(CommandSender sender, String player, String coins) {
        String format = this.coinsAddMoney;
        format = UtilityMethods.replaceAll(PLAYER_PATTERN, format, ()-> player);
        format = UtilityMethods.replaceAll(COINS_PATTERN, format, ()-> coins);
        sender.sendMessage(format);
    }

    public void sendCoinsSetMoney(CommandSender sender, String player, String coins) {
        String format = this.coinsSetMoney;
        format = UtilityMethods.replaceAll(PLAYER_PATTERN, format, ()-> player);
        format = UtilityMethods.replaceAll(COINS_PATTERN, format, ()-> coins);
        sender.sendMessage(format);
    }

    //tops
    public void sendMoneyTop(CommandSender sender, Collection<String> top) {
        String topFormat = Joiner.on('\n').join(this.getStringList("moneyTop"));
        String topToString = Joiner.on('\n').join(top);
        topFormat = UtilityMethods.replaceAll(TOP_PATTERN, topFormat, ()-> topToString);
        sender.sendMessage(UtilityMethods.color(topFormat));
    }

    public void sendCoinsTop(CommandSender sender, Collection<String> top) {
        String topFormat = Joiner.on('\n').join(this.getStringList("coinsTop"));
        String topToString = Joiner.on('\n').join(top);
        topFormat = UtilityMethods.replaceAll(TOP_PATTERN, topFormat, ()-> topToString);
        sender.sendMessage(UtilityMethods.color(topFormat));
    }
}
