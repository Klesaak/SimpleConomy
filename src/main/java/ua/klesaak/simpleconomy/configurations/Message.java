package ua.klesaak.simpleconomy.configurations;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;

@Getter
public class Message {
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    protected String miniMessage;

    public Message(String miniMessage) {
        this.miniMessage = miniMessage;
    }

    public TagMessage tag(String pattern, Object replacement) {
        return new TagMessage(this.miniMessage.replace(pattern, String.valueOf(replacement)));
    }

    public void send(CommandSender sender) {
        SimpleEconomyManager.BUKKIT_AUDIENCES.sender(sender).sendMessage(MINI_MESSAGE.deserialize(this.miniMessage));
    }

    public void broadcast() {
        Component message = MINI_MESSAGE.deserialize(this.miniMessage);
        Bukkit.getOnlinePlayers().forEach(player -> SimpleEconomyManager.BUKKIT_AUDIENCES.sender(player).sendMessage(message));
        SimpleEconomyManager.BUKKIT_AUDIENCES.sender(Bukkit.getConsoleSender()).sendMessage(message);
    }

    public static class TagMessage extends Message {

        public TagMessage(String message) {
            super(message);
        }

        @Override
        public TagMessage tag(String pattern, Object replacement) {
            this.miniMessage = this.miniMessage.replace(pattern, String.valueOf(replacement));
            return this;
        }
    }
}
