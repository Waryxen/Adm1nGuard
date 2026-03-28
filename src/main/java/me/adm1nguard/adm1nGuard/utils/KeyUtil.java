package me.adm1nguard.adm1nGuard.utils;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public final class KeyUtil {

    private KeyUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Safe key name for any Keyed object.
     * Example: minecraft:diamond_sword -> diamond_sword
     */
    public static String key(Keyed keyed) {
        if (keyed == null) return "unknown";

        try {
            NamespacedKey key = keyed.getKey();
            if (key != null) {
                return key.getKey();
            }
        } catch (Throwable ignored) {
        }

        return "unknown";
    }

    /**
     * Safe key name for Material.
     * Example: DIAMOND_SWORD -> diamond_sword
     */
    @SuppressWarnings("deprecation")
    public static String material(Material material) {
        if (material == null) return "unknown";

        try {
            NamespacedKey key = material.getKey();
            if (key != null) {
                return key.getKey();
            }
        } catch (Throwable ignored) {
        }

        try {
            return material.name().toLowerCase(Locale.ROOT);
        } catch (Throwable ignored) {
        }

        return "unknown";
    }

    /**
     * Safe key name for Attribute.
     * Example: GENERIC_ATTACK_DAMAGE -> generic_attack_damage
     */
    @SuppressWarnings("all")
    public static String attribute(Attribute attribute) {
        if (attribute == null) return "unknown";

        try {
            NamespacedKey key = attribute.getKey();
            if (key != null) {
                return key.getKey();
            }
        } catch (Throwable ignored) {
        }

        try {
            return attribute.name().toLowerCase(Locale.ROOT);
        } catch (Throwable ignored) {
        }

        return "unknown";
    }

    /**
     * Pretty display name from any raw key.
     * Example: diamond_sword -> Diamond Sword
     */
    public static String pretty(String input) {
        if (input == null || input.isBlank()) return "Unknown";

        return Arrays.stream(input.replace(':', '_').split("_"))
                .filter(part -> !part.isBlank())
                .map(part -> part.substring(0, 1).toUpperCase(Locale.ROOT)
                        + part.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" "));
    }

    /**
     * Pretty display name for Keyed.
     */
    public static String display(Keyed keyed) {
        return pretty(key(keyed));
    }

    /**
     * Pretty display name for Material.
     */
    public static String display(Material material) {
        return pretty(material(material));
    }

    /**
     * Pretty display name for Attribute.
     */
    public static String display(Attribute attribute) {
        return pretty(attribute(attribute));
    }
}