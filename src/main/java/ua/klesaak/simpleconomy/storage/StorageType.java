package ua.klesaak.simpleconomy.storage;

import lombok.Getter;

@Getter
public enum StorageType {
    // Config file based
    FILE("JSON", "json"),
    REDIS("REDIS", "redis"),
    MYSQL("MySQL", "mysql"),
    POSTGRESQL("PostgresSQL", "postgresql"),
    MARIADB("MariaDB", "mariadb");

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