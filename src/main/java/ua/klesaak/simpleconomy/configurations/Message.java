package ua.klesaak.simpleconomy.configurations;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ua.klesaak.simpleconomy.utils.UtilityMethods;

import java.util.regex.Pattern;

@Getter
public final class Message {
    private final String message;

    public Message(String message) {
        this.message = message;
    }

    public TagMessage tag(Pattern pattern, Object replacement) {
        return new TagMessage(UtilityMethods.replaceAll(pattern, this.message, ()-> String.valueOf(replacement)));
    }

    public void send(CommandSender sender) {
        sender.sendMessage(this.message);
    }

    public void broadcast() {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(this.message));
        Bukkit.getConsoleSender().sendMessage(this.message);
    }

    public static class TagMessage {
        private String message;

        public TagMessage(String message) {
            this.message = message;
        }

        public TagMessage tag(Pattern pattern, Object replacement) {
            this.message = UtilityMethods.replaceAll(pattern, this.message, ()-> String.valueOf(replacement));
            return this;
        }

        public void send(CommandSender sender) {
            sender.sendMessage(this.message);
        }

        public void broadcast() {
            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(this.message));
            Bukkit.getConsoleSender().sendMessage(this.message);
        }
    }
}
