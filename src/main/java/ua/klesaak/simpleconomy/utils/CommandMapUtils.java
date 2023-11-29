package ua.klesaak.simpleconomy.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;

public final class CommandMapUtils {
    private static final CommandMap COMMAND_MAP;

    private CommandMapUtils() {
    }

    public static CommandMap getMap() {
        return COMMAND_MAP;
    }

    public static Command getCommandByAlias(String alias) {
        return COMMAND_MAP.getCommand(alias);
    }
    
    public static void registerCommand(String label, Command command) {
        if (COMMAND_MAP.getCommand(label.toLowerCase()) == null) {
            COMMAND_MAP.register(label, command);
        }
    }

    public static void unRegisterCommand(String label) {
        if (COMMAND_MAP.getCommand(label.toLowerCase()) != null) {
            Objects.requireNonNull(COMMAND_MAP.getCommand(label)).unregister(COMMAND_MAP);
        }
    }

    public static Command getCommand(String label) {
        return COMMAND_MAP.getCommand(label.toLowerCase());
    }

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            COMMAND_MAP = (CommandMap) lookup.findVirtual(Bukkit.getServer().getClass(), "getCommandMap", MethodType.methodType(SimpleCommandMap.class)).invoke(Bukkit.getServer());
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}