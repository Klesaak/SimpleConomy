package ua.klesaak.simpleconomy.configurations;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Pattern;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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


    public MessagesFile(JavaPlugin plugin) {
        super(plugin, "messages.yml");
    }


}
