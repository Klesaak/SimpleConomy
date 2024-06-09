package ua.klesaak.simpleconomy.manager;

import com.google.common.base.Joiner;
import lombok.Data;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import ua.klesaak.simpleconomy.configurations.ConfigFile;
import ua.klesaak.simpleconomy.utils.MCColorUtils;
import ua.klesaak.simpleconomy.utils.UtilityMethods;

import java.util.ArrayList;
import java.util.Collection;

import static ua.klesaak.simpleconomy.configurations.MessagesFile.*;

@Getter
public class TopManager implements AutoCloseable {
    private String moneyTop, coinsTop;
    private final SimpleEconomyManager manager;
    private BukkitTask updateTask;

    public TopManager(SimpleEconomyManager manager, ConfigFile configFile) {
        this.manager = manager;
        val moneyTopCount = configFile.getPlayerTopMoneyCount();
        val coinsTopCount = configFile.getPlayerTopCoinsCount();
        val topUpdateInterval = configFile.getPlayerTopUpdateTickInterval();
        this.startUpdateTask(moneyTopCount, coinsTopCount, topUpdateInterval);
    }

    public void startUpdateTask(int moneyTopCount, int coinsTopCount, int topUpdateInterval) {
        this.updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.manager.getPlugin(), ()-> {
            val storage = this.manager.getStorage();
            val messagesFile = this.manager.getMessagesFile();
            val configFile = this.manager.getConfigFile();
            this.moneyTop = messagesFile.getMoneyTopIsEmpty().getMessage();
            val moneyTop = storage.getMoneyTop(moneyTopCount);
            if (!moneyTop.isEmpty()) {
                val list = new ArrayList<String>(moneyTopCount);
                moneyTop.forEach(topLineDouble -> list.add(this.formatTopLine(configFile.getTopFormat(), topLineDouble.getIndex(), topLineDouble.getNickName(), topLineDouble.getSum())));
                this.moneyTop = this.format(messagesFile.getMoneyTopFormat(), list);
            }

            this.coinsTop = messagesFile.getCoinsTopIsEmpty().getMessage();
            val coinsTop = storage.getCoinsTop(coinsTopCount);
            if (!coinsTop.isEmpty()) {
                val list = new ArrayList<String>(coinsTopCount);
                coinsTop.forEach(topLineInteger -> list.add(this.formatTopLine(configFile.getTopFormat(), topLineInteger.getIndex(), topLineInteger.getNickName(), topLineInteger.getSum())));
                this.coinsTop = this.format(messagesFile.getCoinsTopFormat(), list);
            }
        }, 20L, topUpdateInterval);
    }


    private String format(String format, Collection<String> top) {
        String topToString = Joiner.on('\n').join(top);
        format = UtilityMethods.replaceAll(TOP_PATTERN, format, ()-> topToString);
        return MCColorUtils.color(format);
    }

    public String formatTopLine(String format, int index, String player, Object balance) {
        format = UtilityMethods.replaceAll(INDEX_PATTERN, format, ()-> String.valueOf(index));
        format = UtilityMethods.replaceAll(PLAYER_PATTERN, format, ()-> player);
        format = UtilityMethods.replaceAll(BALANCE_PATTERN, format, ()-> String.valueOf(balance));
        return format;
    }

    @Override
    public void close() {
        if (this.updateTask != null) {
            this.updateTask.cancel();
            this.updateTask = null;
        }
    }

    @Data
    public static class TopLineDouble {
        private final String nickName;
        private final double sum;
        private final int index;
    }

    @Data
    public static class TopLineInteger {
        private final String nickName;
        private final int sum, index;
    }
}
