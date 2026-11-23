package net.klesaak.simpleconomy.manager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SConomyListener implements Listener {
    private final SimpleEconomyManager manager;

    public SConomyListener(SimpleEconomyManager manager) {
        this.manager = manager;
        manager.getPlugin().getServer().getPluginManager().registerEvents(this, manager.getPlugin());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.manager.getStorage().cache(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.manager.getStorage().unCache(event.getPlayer().getName());
    }

}
