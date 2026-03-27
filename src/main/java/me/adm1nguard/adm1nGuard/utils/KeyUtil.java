package me.adm1nguard.adm1nGuard.utils;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;

public final class KeyUtil {

    private KeyUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Safe key name for any Keyed object (modern Bukkit/Paper API)
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
     * Falls back to old enum name if needed.
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
            return material.name().toLowerCase();
        } catch (Throwable ignored) {
        }

        return "unknown";
    }

    /**
     * Safe key name for Attribute.
     * Falls back to old enum name if needed.
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
            return attribute.name().toLowerCase();
        } catch (Throwable ignored) {
        }

        return "unknown";
    }
}