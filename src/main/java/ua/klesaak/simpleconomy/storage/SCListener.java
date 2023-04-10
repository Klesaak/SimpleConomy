package ua.klesaak.simpleconomy.storage;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ua.klesaak.simpleconomy.manager.PlayerData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SCListener implements Listener {
    private final Map<String, PlayerData> playerCache = new ConcurrentHashMap<>(Bukkit.getMaxPlayers());

    public SCListener(JavaPlugin javaPlugin) {
        javaPlugin.getServer().getPluginManager().registerEvents(this, javaPlugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {

    }
}
