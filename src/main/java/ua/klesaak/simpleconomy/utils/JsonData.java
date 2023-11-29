package ua.klesaak.simpleconomy.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Type;

@Getter
public class JsonData {
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create();
    private final File file;

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

    @SafeVarargs
    public static <T, V> Map<T, V> mapOf(Pair<T, V>... pairs) {
        val map = new HashMap<T, V>();
        for (Pair<T, V> pair : pairs) {
            map.putIfAbsent(pair.key, pair.value);
        }
        return map;
    }

    public static <T, V> Pair<T, V> pairOf(T key, V value) {
        return new Pair<>(key, value);
    }

    @SneakyThrows
    public <T> void write(T source, boolean needTypeToken) {
        String json = needTypeToken ? GSON.toJson(source, new TypeToken<T>(){}.getType()) : GSON.toJson(source);
        Files.write(this.file.toPath(), json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @SneakyThrows
    public <T> void write(T source, Type typeToken) {
        String json = GSON.toJson(source, typeToken);
        Files.write(this.file.toPath(), json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Getter
    public static class Pair<T, V> {
        private final T key;
        private final V value;

        public Pair(T key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
