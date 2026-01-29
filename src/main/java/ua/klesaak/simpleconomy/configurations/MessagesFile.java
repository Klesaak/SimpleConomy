package ua.klesaak.simpleconomy.configurations;

import com.google.common.base.Joiner;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class MessagesFile extends PluginConfig {
    public static final String BALANCE_PATTERN     = "(balance)";
    public static final String NEW_BALANCE_PATTERN = "(new-balance)";
    public static final String MAX_BALANCE_PATTERN = "(max-balance)";
    public static final String LABEL_PATTERN       = "(label)";
    public static final String COINS_PATTERN       = "(coins)";
    public static final String PLAYER_PATTERN      = "(player)";
    public static final String MONEY_PATTERN       = "(money)";
    public static final String SUM_PATTERN         = "(sum)";
    public static final String TOP_PATTERN         = "(top)";
    public static final String INDEX_PATTERN       = "(index)";
    public static final String NUMBER_PATTERN      = "(number)";

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
            message = Joiner.on("<br>").join(this.getStringList(key));
        }
        return message;
    }
}
