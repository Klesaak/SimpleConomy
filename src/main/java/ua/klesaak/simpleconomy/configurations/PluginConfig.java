package ua.klesaak.simpleconomy.configurations;

import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Level;

public class PluginConfig extends YamlConfiguration {
    private final transient File file;

    public PluginConfig(JavaPlugin plugin, File directory, String configFileName) {
        this.file = new File(directory, configFileName);
        this.createFile(plugin);
    }

    public PluginConfig(JavaPlugin plugin, String configFileName) {
        this.file = new File(plugin.getDataFolder(), configFileName);
        this.createFile(plugin);
    }

    @SneakyThrows
    private void createFile(JavaPlugin plugin) {
        val pluginDataFolderPath = plugin.getDataFolder().toPath();
        if (!Files.exists(pluginDataFolderPath)) {
            Files.createDirectory(pluginDataFolderPath);
            plugin.getLogger().log(Level.WARNING, "successfully create file: " + pluginDataFolderPath);
        }
        if (!this.file.exists()) Files.copy(Objects.requireNonNull(plugin.getResource(file.getName())), file.toPath());
        this.load(this.file);
    }

    @SneakyThrows
    public void save() {
        this.save(this.file);
    }
}
