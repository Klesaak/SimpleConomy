package ua.klesaak.simpleconomy.configurations;

import com.google.common.base.Joiner;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import ua.klesaak.simpleconomy.utils.UtilityMethods;

import java.util.Collection;
import java.util.regex.Pattern;

@Getter
public class MessagesFile extends PluginConfig {
    public static final Pattern BALANCE_PATTERN = Pattern.compile("(balance)", Pattern.LITERAL);
    public static final Pattern NEW_BALANCE_PATTERN = Pattern.compile("(new-balance)", Pattern.LITERAL);
    public static final Pattern MAX_BALANCE_PATTERN = Pattern.compile("(max-balance)", Pattern.LITERAL);
    public static final Pattern LABEL_PATTERN = Pattern.compile("(label)", Pattern.LITERAL);
    public static final Pattern COINS_PATTERN = Pattern.compile("(coins)", Pattern.LITERAL);
    public static final Pattern PLAYER_PATTERN = Pattern.compile("(player)", Pattern.LITERAL);
    public static final Pattern MONEY_PATTERN = Pattern.compile("(money)", Pattern.LITERAL);
    public static final Pattern SUM_PATTERN = Pattern.compile("(sum)", Pattern.LITERAL);
    public static final Pattern TOP_PATTERN = Pattern.compile("(top)", Pattern.LITERAL);
    public static final Pattern INDEX_PATTERN = Pattern.compile("(index)", Pattern.LITERAL);
    public static final Pattern NUMBER_PATTERN = Pattern.compile("(number)", Pattern.LITERAL);

    private final Message balanceInfo, balanceInfoOther, vaultPaySuccessful, vaultPayErrorMaxBalance, vaultNoMoney, vaultPayReceived, errorMinTransaction, vaultNoPlayerMoney, vaultPayUsage;
    private final Message vaultSenderWithdrawn, playerNotFound, notInteger, paySelf, vaultAddMoney, vaultSetMoney;
    private final Message coinsNoPlayerMoney, coinsSenderWithdrawn, coinsAddMoney, coinsSetMoney;


    public MessagesFile(JavaPlugin plugin) {
        super(plugin, "messages.yml");
        this.balanceInfo = new Message(this.getStringValue("balanceInfo"));
        this.balanceInfoOther = new Message(this.getStringValue("balanceInfoOther"));
        this.vaultPaySuccessful = new Message(this.getStringValue("vaultPaySuccessful"));
        this.vaultPayErrorMaxBalance = new Message(this.getStringValue("vaultPayErrorMaxBalance"));
        this.vaultNoMoney = new Message(this.getStringValue("vaultNoMoney"));
        this.vaultPayReceived = new Message(this.getStringValue("vaultPayReceived"));
        this.errorMinTransaction = new Message(this.getStringValue("errorMinTransaction"));
        this.vaultNoPlayerMoney = new Message(this.getStringValue("vaultNoPlayerMoney"));
        this.vaultPayUsage = new Message(this.getStringValue("vaultPayUsage"));
        this.vaultSenderWithdrawn = new Message(this.getStringValue("vaultSenderWithdrawn"));
        this.playerNotFound = new Message(this.getStringValue("playerNotFound"));
        this.notInteger = new Message(this.getStringValue("notInteger"));
        this.paySelf = new Message(this.getStringValue("paySelf"));
        this.vaultAddMoney = new Message(this.getStringValue("vaultAddMoney"));
        this.vaultSetMoney = new Message(this.getStringValue("vaultSetMoney"));
        this.coinsNoPlayerMoney = new Message(this.getStringValue("coinsNoPlayerMoney"));
        this.coinsSenderWithdrawn = new Message(this.getStringValue("coinsSenderWithdrawn"));
        this.coinsAddMoney = new Message(this.getStringValue("coinsAddMoney"));
        this.coinsSetMoney = new Message(this.getStringValue("coinsSetMoney"));
    }

    private String getStringValue(String key) {
        String message = "";
        if (this.isString(key)) {
            message = this.getString(key);
        } else if (this.isList(key)) {
            message = Joiner.on('\n').join(this.getStringList(key));
        }
        return UtilityMethods.color(message);
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
