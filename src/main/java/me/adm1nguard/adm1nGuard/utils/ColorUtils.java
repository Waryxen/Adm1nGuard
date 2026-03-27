package me.adm1nguard.adm1nGuard.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;

public class ColorUtils {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Translates a string containing MiniMessage tags or & codes
     * into an Adventure Component.
     *
     * @param message the input string
     * @return a Component with proper formatting
     */
    public static Component parse(String message) {
        if (message == null || message.isEmpty()) return Component.empty();

        // First, replace & codes with Bukkit ChatColor codes
        String legacy = translateAmpersand(message);

        // Attempt MiniMessage parsing
        try {
            return MINI_MESSAGE.deserialize(legacy);
        } catch (Exception e) {
            // Fallback to legacy colours if MiniMessage fails
            return LegacyComponentSerializer.legacyAmpersand().deserialize(legacy);
        }
    }

    /**
     * Strips all colour and formatting codes from a string.
     * Handles &-codes, §-codes, and MiniMessage tags.
     *
     * @param message the input string
     * @return a plain string with all formatting removed
     */
    @SuppressWarnings("deprecation")
    public static String stripColor(String message) {
        if (message == null || message.isEmpty()) return message;

        // Strip MiniMessage tags by deserializing then extracting plain text
        String stripped;
        try {
            Component component = MINI_MESSAGE.deserialize(message);
            stripped = PlainTextComponentSerializer.plainText().serialize(component);
        } catch (Exception e) {
            stripped = message;
        }

        // Strip any remaining § or & colour codes (e.g. §a, &c)
        return ChatColor.stripColor(stripped.replace("&", "§"));
    }

    /**
     * Translates & colour codes to Bukkit ChatColor codes.
     */
    @SuppressWarnings("deprecation")
    private static String translateAmpersand(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Simple helper to translate a Component
     */
    public static String toLegacyString(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }
}