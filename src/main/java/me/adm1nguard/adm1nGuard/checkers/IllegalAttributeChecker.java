package me.adm1nguard.adm1nGuard.checkers;

import com.google.common.collect.Multimap;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class IllegalAttributeChecker {

    // Custom maximum values for attributes (positive limits)
    private static final Map<Attribute, Double> CUSTOM_MAX = Map.of(
            // Example: limit attack damage to 10
            //Attribute.ATTACK_DAMAGE, 10.0,
            //Attribute.MAX_HEALTH, 40.0
    );

    // TODO: Make these configurable via config

    // Custom minimum values for attributes (optional, for illegal negative checks)
    private static final Map<Attribute, Double> CUSTOM_MIN = Map.of(
            // Example: prevent health below 1
            //Attribute.MAX_HEALTH, 1.0
    );

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

        return modifiers.asMap().entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .filter(mod -> isIllegal(entry.getKey(), mod))
                        .map(mod -> Map.entry(entry.getKey(), mod)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toUnmodifiableList())
                ));
    }

    /**
     * Checks if a specific modifier is illegal based on configured limits.
     */
    private static boolean isIllegal(Attribute attribute, AttributeModifier modifier) {
        double maxAllowed = CUSTOM_MAX.getOrDefault(attribute, Double.MAX_VALUE);
        double minAllowed = CUSTOM_MIN.getOrDefault(attribute, Double.NEGATIVE_INFINITY);

        double amount = modifier.getAmount();

        // Illegal if below min or above max
        return amount < minAllowed || amount > maxAllowed;
    }
}
