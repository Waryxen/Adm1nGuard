package me.adm1nguard.adm1nGuard.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class MessageUtils {

    // Default prefix string (supports & codes or MiniMessage)
    private static final String prefixString = "&7[&bAdm1nGuard&7] &f";
    private static final Component prefixComponent = ColorUtils.parse(prefixString);

    /**
     * Sends a message to a CommandSender (Player or Console) with prefix.
     * Supports MiniMessage tags and & codes via ColorUtils.
     *
     * @param sender  the recipient
     * @param message the message string
     */
    public static void sendMessage(CommandSender sender, String message) {
        if (sender == null || message == null || message.isEmpty()) return;

        // Parse the message with ColorUtils
        Component messageComponent = ColorUtils.parse(message);

        // Combine prefix and message
        Component full = prefixComponent.append(messageComponent);

        sender.sendMessage(full);
    }

    /**
     * Sends a pre-built Component with prefix.
     *
     * @param sender  the recipient
     * @param message the Component message
     */
    public static void sendMessage(CommandSender sender, Component message) {
        if (sender == null || message == null) return;

        Component full = prefixComponent.append(message);
        sender.sendMessage(full);
    }
}
