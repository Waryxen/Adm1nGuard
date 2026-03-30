package me.adm1nguard.adm1nGuard.utils;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
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
            return key.getKey();
        } catch (Throwable ignored) {
        }

        return fallbackName(keyed);
    }

    /**
     * Safe key name for Material.
     * Example: DIAMOND_SWORD -> diamond_sword
     */
    public static String material(Material material) {
        if (material == null) return "unknown";

        String namespaced = tryNamespacedKey(material);
        if (namespaced != null) {
            int index = namespaced.indexOf(':');
            return index >= 0 ? namespaced.substring(index + 1) : namespaced;
        }

        return material.name().toLowerCase(Locale.ROOT);
    }

    /**
     * Safe key name for Attribute.
     * Example: minecraft:attack_damage -> attack_damage
     */
    public static String attribute(Attribute attribute) {
        if (attribute == null) return "unknown";

        String namespaced = tryNamespacedKey(attribute);
        if (namespaced != null) {
            int index = namespaced.indexOf(':');
            return index >= 0 ? namespaced.substring(index + 1) : namespaced;
        }

        return fallbackName(attribute);
    }

    /**
     * Pretty display name from any raw key.
     * Example: diamond_sword -> Diamond Sword
     */
    public static String pretty(String input) {
        if (input == null || input.isBlank()) return "Unknown";

        return Arrays.stream(input.replace(':', '_').split("_"))
                .filter(part -> !part.isBlank())
                .map(part -> Character.toUpperCase(part.charAt(0)) +
                        part.substring(1).toLowerCase(Locale.ROOT))
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

    /**
     * Full namespaced key for any Keyed object.
     * Example: minecraft:diamond_sword
     */
    public static String namespaced(Keyed keyed) {
        if (keyed == null) return "unknown:unknown";

        try {
            NamespacedKey key = keyed.getKey();
            return key.toString();
        } catch (Throwable ignored) {
        }

        return "minecraft:" + fallbackName(keyed);
    }

    /**
     * Full namespaced key for Material.
     */
    public static String namespaced(Material material) {
        if (material == null) return "unknown:unknown";

        String namespaced = tryNamespacedKey(material);
        if (namespaced != null) {
            return namespaced;
        }

        return "minecraft:" + material.name().toLowerCase(Locale.ROOT);
    }

    /**
     * Full namespaced key for Attribute.
     */
    public static String namespaced(Attribute attribute) {
        if (attribute == null) return "unknown:unknown";

        String namespaced = tryNamespacedKey(attribute);
        if (namespaced != null) {
            return namespaced;
        }

        return "minecraft:" + fallbackName(attribute);
    }

    /**
     * Tries to call getKey() reflectively for compatibility with older APIs.
     */
    private static String tryNamespacedKey(Object object) {
        if (object == null) return null;

        try {
            Method method = object.getClass().getMethod("getKey");
            Object result = method.invoke(object);
            if (result instanceof NamespacedKey key) {
                return key.toString();
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    /**
     * Fallback for enums / objects without getKey().
     */
    private static String fallbackName(Object object) {
        if (object == null) return "unknown";

        if (object instanceof Enum<?> enumValue) {
            return enumValue.name().toLowerCase(Locale.ROOT);
        }

        return object.toString().toLowerCase(Locale.ROOT);
    }
}
