package ua.klesaak.simpleconomy.manager;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;

public class TopManager {
    private LinkedList<String> moneyTop, coinsTop;
    private final SimpleEconomyManager manager;
    private BukkitTask updateTask;

    public TopManager(SimpleEconomyManager manager, int moneyTopCount, int coinsTopCount, int topUpdateInterval) {
        this.manager = manager;
        this.moneyTop = new LinkedList<>();
        this.coinsTop = new LinkedList<>();
        this.startUpdateTask(topUpdateInterval);
    }

    public void startUpdateTask(int topUpdateInterval) {
        this.updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.manager.getPlugin(), ()-> {
            //todo
        }, topUpdateInterval, topUpdateInterval);
    }
}
