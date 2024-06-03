package ua.klesaak.simpleconomy.manager;

import lombok.Data;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import ua.klesaak.simpleconomy.configurations.ConfigFile;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TopManager implements AutoCloseable {
    private List<String> moneyTop, coinsTop;
    private final SimpleEconomyManager manager;
    private BukkitTask updateTask;

    public TopManager(SimpleEconomyManager manager, ConfigFile configFile) {
        this.manager = manager;
        val moneyTopCount = configFile.getPlayerTopMoneyCount();
        val coinsTopCount = configFile.getPlayerTopCoinsCount();
        val topUpdateInterval = configFile.getPlayerTopUpdateTickInterval();
        this.moneyTop = new ArrayList<>(moneyTopCount);
        this.coinsTop = new ArrayList<>(coinsTopCount);
        this.startUpdateTask(moneyTopCount, coinsTopCount, topUpdateInterval);
    }

    public void startUpdateTask(int moneyTopCount, int coinsTopCount, int topUpdateInterval) {
        this.updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.manager.getPlugin(), ()-> {
            val storage = manager.getStorage();
            this.moneyTop = new ArrayList<>(moneyTopCount);
            storage.getMoneyTop(moneyTopCount).forEach(topLineDouble -> this.moneyTop.add(this.manager.getConfigFile().formatTopLine(topLineDouble.getIndex(), topLineDouble.getNickName(), topLineDouble.getSum())));

            this.coinsTop = new ArrayList<>();
            storage.getCoinsTop(coinsTopCount).forEach(topLineInteger -> this.coinsTop.add(this.manager.getConfigFile().formatTopLine(topLineInteger.getIndex(), topLineInteger.getNickName(), topLineInteger.getSum())));

        }, 20L, topUpdateInterval);
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
