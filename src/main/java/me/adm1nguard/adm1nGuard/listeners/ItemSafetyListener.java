package me.adm1nguard.adm1nGuard.listeners;

import me.adm1nguard.adm1nGuard.Adm1nGuard;
import me.adm1nguard.adm1nGuard.checkers.IllegalAttributeChecker;
import me.adm1nguard.adm1nGuard.checkers.IllegalEnchantChecker;
import me.adm1nguard.adm1nGuard.utils.KeyUtil;
import me.adm1nguard.adm1nGuard.utils.PermissionUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemSafetyListener implements Listener {

    private final Adm1nGuard plugin;
    private final Set<Material> blacklistedMaterials = new HashSet<>();

    public ItemSafetyListener(Adm1nGuard plugin) {
        this.plugin = plugin;
        loadBlacklistFromConfig();
    }

    private void loadBlacklistFromConfig() {
        List<String> items = plugin.getConfig().getStringList("blacklisted-items");
        blacklistedMaterials.clear();

        for (String itemName : items) {
            try {
                blacklistedMaterials.add(Material.valueOf(itemName.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Invalid blacklisted item in config: " + itemName);
            }
        }
    }

    private List<String> getReasons(ItemStack item) {
        List<String> reasons = new ArrayList<>();
        if (item == null) return reasons;

        // Blacklisted material
        if (blacklistedMaterials.contains(item.getType())) {
            reasons.add("Blacklisted Item: " + KeyUtil.material(item.getType()));
        }

        // Illegal enchants
        Map<Enchantment, Integer> illegalEnchants = IllegalEnchantChecker.getIllegalEnchants(item);
        illegalEnchants.forEach((enchant, level) ->
                reasons.add("Illegal Enchant: " + KeyUtil.key(enchant) + " " + level)
        );

        // Illegal attributes
        Map<Attribute, List<AttributeModifier>> illegalAttributes =
                IllegalAttributeChecker.getIllegalAttributes(item);

        illegalAttributes.forEach((attribute, modifiers) -> {
            for (AttributeModifier modifier : modifiers) {
                reasons.add("Illegal Attribute: " + KeyUtil.attribute(attribute)
                        + " (amount: " + modifier.getAmount() + ")");
            }
        });

        return reasons;
    }

    private void handle(Player player, ItemStack item, List<String> reasons) {
        if (reasons.isEmpty()) return;
        plugin.handleIllegalItem(player, item, reasons);
    }

    private void scanInventory(Player player) {
        if (PermissionUtil.hasBypass(player)) return;

        for (ItemStack item : player.getInventory().getContents()) {
            List<String> reasons = getReasons(item);
            if (!reasons.isEmpty()) {
                handle(player, item, reasons);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        scanInventory(event.getPlayer());
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (PermissionUtil.hasBypass(player)) return;

        ItemStack item = event.getItem().getItemStack();
        List<String> reasons = getReasons(item);

        if (!reasons.isEmpty()) {
            handle(player, item, reasons);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (PermissionUtil.hasBypass(player)) return;

        ItemStack current = event.getCurrentItem();
        List<String> reasons = getReasons(current);

        if (!reasons.isEmpty()) {
            handle(player, current, reasons);
            event.setCancelled(true);
        }
    }
}