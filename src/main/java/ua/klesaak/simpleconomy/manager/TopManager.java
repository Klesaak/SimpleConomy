package ua.klesaak.simpleconomy.manager;

import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import ua.klesaak.simpleconomy.configurations.ConfigFile;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TopManager {
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
            this.moneyTop = storage.getMoneyTop(moneyTopCount);
            this.coinsTop = storage.getCoinsTop(coinsTopCount);
        }, 20L, topUpdateInterval);
    }
}
