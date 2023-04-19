package ua.klesaak.simpleconomy.manager;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;

public class TopManager {
    private final LinkedList<String> moneyTop, coinsTop;
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
            this.coinsTop.clear();
            this.moneyTop.clear();;
            //todo заполнять списки уже отформатированными топами
        }, topUpdateInterval, topUpdateInterval);
    }
}
