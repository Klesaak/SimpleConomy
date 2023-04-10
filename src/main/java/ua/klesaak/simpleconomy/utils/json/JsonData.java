package ua.klesaak.simpleconomy.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class JsonData {
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create();
    @Getter private final File file;

    @SneakyThrows
    public JsonData(File file) {
        if (!file.getParentFile().exists()) Files.createDirectory(file.getParentFile().toPath());
        if (!file.exists()) Files.createFile(file.toPath());
        this.file = file;
    }

    @SneakyThrows
    public <T> T readAll(TypeToken<? extends T> typeToken) {
        return GSON.fromJson(new String(Files.readAllBytes(this.file.toPath())), typeToken.getType());
    }

    @SneakyThrows
    public <T> T readAll(Class<? extends T> clazz) {
        return GSON.fromJson(new String(Files.readAllBytes(this.file.toPath())), clazz);
    }

    @SneakyThrows
    public <T> void write(T source, boolean needTypeToken) {
        String json = needTypeToken ? GSON.toJson(source, new TypeToken<T>(){}.getType()) : GSON.toJson(source);
        Files.write(this.file.toPath(), json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
