package ua.klesaak.simpleconomy.storage;

import lombok.Getter;

@Getter
public enum StorageType {
    FILE("JSON", "file"),
    REDIS("REDIS", "jedis");

    private final String name;

    private final String identifier;

    StorageType(String name, String identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    public static StorageType parse(String name, StorageType def) {
        for (StorageType t : values()) {
            if (t.getIdentifier().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return def;
    }
}