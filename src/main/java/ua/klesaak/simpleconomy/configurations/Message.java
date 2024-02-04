package ua.klesaak.simpleconomy.configurations;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ua.klesaak.simpleconomy.utils.UtilityMethods;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public final class Message {
    private final String message;

    public Message(String message) {
        this.message = message;
    }

    public Message tag(Pattern pattern, Object replacement) {
        return new Message(UtilityMethods.replaceAll(pattern, this.message, ()-> String.valueOf(replacement)));
    }

    public void send(CommandSender sender) {
        sender.sendMessage(this.message);
    }

    public void broadcast() {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(this.message));
        Bukkit.getConsoleSender().sendMessage(this.message);
    }
}
