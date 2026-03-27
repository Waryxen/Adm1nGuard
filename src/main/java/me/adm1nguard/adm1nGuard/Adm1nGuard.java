package me.adm1nguard.adm1nGuard;

import me.adm1nguard.adm1nGuard.checkers.IllegalAttributeChecker;
import me.adm1nguard.adm1nGuard.checkers.IllegalEnchantChecker;
import me.adm1nguard.adm1nGuard.listeners.ItemSafetyListener;
import me.adm1nguard.adm1nGuard.utils.ColorUtils;
import me.adm1nguard.adm1nGuard.utils.KeyUtil;
import me.adm1nguard.adm1nGuard.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Adm1nGuard extends JavaPlugin {

    private static Adm1nGuard instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new ItemSafetyListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Adm1nGuard getInstance() {
        return instance;
    }

    public void handleIllegalItem(Player player, ItemStack item, List<String> reasons) {
        if (item == null) return;

        List<String> violations = new ArrayList<>();

        Map<Enchantment, Integer> illegalEnchants = IllegalEnchantChecker.getIllegalEnchants(item);
        illegalEnchants.forEach((enchant, level) ->
                violations.add("&cEnchant: &6" + KeyUtil.key(enchant) + " " + level)
        );

        Map<Attribute, List<AttributeModifier>> illegalAttributes =
                IllegalAttributeChecker.getIllegalAttributes(item);

        illegalAttributes.forEach((attribute, modifiers) -> {
            for (AttributeModifier modifier : modifiers) {
                violations.add("&cAttribute: &6" + KeyUtil.attribute(attribute)
                        + " &7(amount: &e" + modifier.getAmount() + "&7)");
            }
        });
        List<String> blacklistedItems = getConfig().getStringList("blacklisted-items");
        if (blacklistedItems.contains(item.getType().getKey().getKey())) {
            violations.add("&cBlacklisted Item: &6" + KeyUtil.material(item.getType()));
        }
        if (reasons != null && !reasons.isEmpty()) {
            violations.addAll(reasons);
        }

        if (violations.isEmpty()) return;

        String violationsString = String.join("&c, ", violations);

        MessageUtils.sendMessage(player, "&cIllegal item detected: " + violationsString);

        // Notify staff
        String staffMessage = "&e[Staff Alert] &c" + player.getName()
                + " had illegal item: " + violationsString;

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("adm1nguard.staff"))
                .forEach(p -> MessageUtils.sendMessage(p, staffMessage));
        player.getInventory().remove(item);

        getLogger().warning(player.getName() + " had illegal item: "
                + KeyUtil.material(item.getType())
                + " -> " + ColorUtils.stripColor(violationsString.replace("&", "§")));
    }
}
