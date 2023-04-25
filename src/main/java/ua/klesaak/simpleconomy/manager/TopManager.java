package ua.klesaak.simpleconomy.manager;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.List;

@Getter
public class TopManager {
    private List<String> moneyTop, coinsTop;
    private final SimpleEconomyManager manager;
    private BukkitTask updateTask;

    public TopManager(SimpleEconomyManager manager, int moneyTopCount, int coinsTopCount, int topUpdateInterval) {
        this.manager = manager;
        this.moneyTop = new LinkedList<>();
        this.coinsTop = new LinkedList<>();
        this.startUpdateTask(moneyTopCount, coinsTopCount, topUpdateInterval);
    }

    public void startUpdateTask(int moneyTopCount, int coinsTopCount, int topUpdateInterval) {
        this.updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.manager.getPlugin(), ()-> {
            this.moneyTop = manager.getStorage().getMoneyTop(moneyTopCount);
            this.coinsTop = manager.getStorage().getCoinsTop(coinsTopCount);
        }, 20L, topUpdateInterval);
    }
}
