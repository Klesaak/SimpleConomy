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
    private List<TopLineDouble> moneyTopData = new ArrayList<>();//кешируем топ, чтобы каждый раз не ебать бд
    private List<TopLineInteger> coinsTopData = new ArrayList<>();
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
            this.moneyTopData = storage.getMoneyTop(moneyTopCount);
            this.coinsTopData = storage.getCoinsTop(coinsTopCount);
        }, 20L, topUpdateInterval);
    }

    @Override
    public void close() {
        if (this.updateTask != null) {
            this.updateTask.cancel();
            this.updateTask = null;
        }
    }

    public List<TopLineDouble> getMoneyTopData() {
        return new ArrayList<>(this.moneyTopData);
    }

    public List<TopLineInteger> getCoinsTopData() {
        return new ArrayList<>(this.coinsTopData);
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
