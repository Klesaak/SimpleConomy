package ua.klesaak.simpleconomy.utils.json;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Optional;

public class JsonDataService {
    private final Plugin plugin;

    public JsonDataService(Plugin plugin) {
        this.plugin = plugin;
    }

    public JsonData createJsonData(String fileName) {
        return new JsonData(new File(this.plugin.getDataFolder(), fileName + ".json"));
    }

    public JsonData createJsonData(String path, String fileName) {
        return this.createJsonData(path + File.separator + fileName);
    }

    public Optional<JsonData> getJsonData(String name) {
        return this.get(name);
    }

    public Optional<JsonData> getJsonData(String path, String name) {
        return this.get(path + File.separator + name);
    }

    private Optional<JsonData> get(String path) {
        File file = new File(this.plugin.getDataFolder(), path + ".json");
        if (file.exists()) return Optional.of(new JsonData(file));
        return Optional.empty();
    }
}
