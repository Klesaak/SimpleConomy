package ua.klesaak.simpleconomy.configurations;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ua.klesaak.simpleconomy.utils.UtilityMethods;

import java.util.regex.Pattern;

@Getter
public class Message {
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    protected String miniMessage;

    public Message(String miniMessage) {
        this.miniMessage = miniMessage;
    }

    public TagMessage tag(Pattern pattern, Object replacement) {
        return new TagMessage(UtilityMethods.replaceAll(pattern, this.miniMessage, ()-> String.valueOf(replacement)));
    }

    public void send(CommandSender sender) {
        sender.sendMessage(MINI_MESSAGE.deserialize(this.miniMessage));
    }

    public void broadcast() {
        Component message = MINI_MESSAGE.deserialize(this.miniMessage);
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static class TagMessage extends Message {

        public TagMessage(String message) {
            super(message);
        }

        @Override
        public TagMessage tag(Pattern pattern, Object replacement) {
            this.miniMessage = UtilityMethods.replaceAll(pattern, this.miniMessage, ()-> String.valueOf(replacement));
            return this;
        }
    }
}
