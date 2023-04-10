package ua.klesaak.simpleconomy.storage.file;

import org.bukkit.plugin.java.JavaPlugin;
import ua.klesaak.simpleconomy.manager.PlayerData;
import ua.klesaak.simpleconomy.storage.IStorage;
import ua.klesaak.simpleconomy.storage.SCListener;

public class JsonStorage extends SCListener implements IStorage {

    public JsonStorage(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    @Override
    public void init() {
    }

    @Override
    public void savePlayer(String nickName, PlayerData playerData) {

    }

    @Override
    public PlayerData getPlayer(String nickName) {
        return null;
    }
}
