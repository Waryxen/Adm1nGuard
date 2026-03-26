package me.adm1nguard.adm1nGuard.checkers;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IllegalEnchantChecker {

    // Optional manual overrides for stricter limits
    private static final Map<Enchantment, Integer> CUSTOM_MAX;
    static {
        // Example: enforce stricter limits
        // map.put(Enchantment.SHARPNESS, 5);
        CUSTOM_MAX = Map.of(); // Immutable to prevent accidental changes
    }

    /**
     * Checks if the given ItemStack has any illegal enchantments.
     *
     * @param item ItemStack to check
     * @return true if illegal enchantments are found, false otherwise
     */
    public static boolean hasIllegalEnchants(ItemStack item) {
        return !getIllegalEnchants(item).isEmpty();
    }

    /**
     * Returns a map of illegal enchantments and their levels for the given item.
     *
     * @param item ItemStack to check
     * @return Map of illegal enchantments and levels
     */
    public static Map<Enchantment, Integer> getIllegalEnchants(ItemStack item) {
        if (item == null || item.getEnchantments().isEmpty()) return Collections.emptyMap();

        Map<Enchantment, Integer> illegal = new HashMap<>();
        item.getEnchantments().forEach((enchant, level) -> {
            int maxAllowed = CUSTOM_MAX.getOrDefault(enchant, enchant.getMaxLevel());

            if (level < enchant.getStartLevel() || level > maxAllowed || !enchant.canEnchantItem(item)) {
                illegal.put(enchant, level);
            }
        });

        return illegal;
    }
}