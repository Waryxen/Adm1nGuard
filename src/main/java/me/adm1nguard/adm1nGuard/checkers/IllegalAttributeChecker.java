package me.adm1nguard.adm1nGuard.checkers;

import com.google.common.collect.Multimap;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class IllegalAttributeChecker {

    // Optional manual overrides for stricter limits
    private static final Map<Attribute, Double> CUSTOM_MAX;
    static {
        // Example: limit attack damage to 10
        // map.put(Attribute.GENERIC_ATTACK_DAMAGE, 10.0);
        CUSTOM_MAX = Map.of();
    }

    /**
     * Checks if the given ItemStack has any illegal attribute modifiers.
     *
     * @param item ItemStack to check
     * @return true if illegal attributes are found, false otherwise
     */
    public static boolean hasIllegalAttributes(ItemStack item) {
        return !getIllegalAttributes(item).isEmpty();
    }

    /**
     * Returns a map of illegal attribute modifiers for the given ItemStack.
     *
     * @param item ItemStack to check
     * @return Map of illegal attributes to their offending modifiers
     */
    public static Map<Attribute, List<AttributeModifier>> getIllegalAttributes(ItemStack item) {
        if (item == null) return Collections.emptyMap();

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return Collections.emptyMap();

        Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();
        if (modifiers == null || modifiers.isEmpty()) return Collections.emptyMap();

        Map<Attribute, List<AttributeModifier>> illegal = new HashMap<>();

        modifiers.asMap().forEach((attribute, mods) -> {
            double maxAllowed = CUSTOM_MAX.getOrDefault(attribute, Double.MAX_VALUE);
            mods.stream()
                    .filter(mod -> mod.getAmount() < 0 || mod.getAmount() > maxAllowed)
                    .forEach(mod -> illegal.computeIfAbsent(attribute, k -> new ArrayList<>()).add(mod));
        });

        return illegal;
    }
}