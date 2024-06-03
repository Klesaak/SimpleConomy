package ua.klesaak.simpleconomy.utils;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.klesaak.simpleconomy.configurations.Message;

import java.util.Optional;

public abstract class AbstractBukkitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String label, String[] args) {
        try {
            this.onReceiveCommand(commandSender, label, args);
        } catch (AbstractCommandException exception) {
            exception.sendMessage(commandSender);
        }
        return false;
    }

    public abstract void onReceiveCommand(CommandSender sender, String label, String[] args);

    public void cmdVerifyArgs(int minimum, String[] args, String usage) {
        if (args.length < minimum) {
            throw new AbstractCommandException(ChatColor.RED + usage);
        }
    }

    public Player cmdVerifyPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            throw new AbstractCommandException(ChatColor.RED + "Must be player");
        }
        return (Player)sender;
    }

    public void cmdVerifyPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            throw new AbstractCommandException(ChatColor.RED + "Do not have permission");
        }
    }

    public void cmdVerifyPermission(CommandSender sender, String permission, String errorMessage) {
        if (!sender.hasPermission(permission)) {
            throw new AbstractCommandException(errorMessage);
        }
    }

    public void cmdVerify(boolean predicate, String usage) {
        if (predicate) {
            throw new AbstractCommandException(ChatColor.RED + usage);
        }
    }

    public <T> T cmdVerifyOptional(Optional<T> optional, String usage) {
        if (!optional.isPresent()) {
            throw new AbstractCommandException(ChatColor.RED + usage);
        }
        return optional.get();
    }

    public int cmdVerifyInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new AbstractCommandException(ChatColor.RED + "Can't cast word to number");
        }
    }

    public static class AbstractCommandException extends RuntimeException {
        private final Message message;

        public AbstractCommandException(Message message) {
            this.message = message;
        }

        public AbstractCommandException(String message) {
            this.message = new Message(message);
        }

        public void sendMessage(CommandSender commandSender) {
            this.message.send(commandSender);
        }
    }
}

