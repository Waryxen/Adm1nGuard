package me.adm1nguard.adm1nGuard.listeners;

import me.adm1nguard.adm1nGuard.Adm1nGuard;
import me.adm1nguard.adm1nGuard.utils.PermissionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class         BlacklistListener implements Listener {

    private final Adm1nGuard plugin;
    private final Set<Material> blacklistedMaterials = new HashSet<>();

    public BlacklistListener(Adm1nGuard plugin) {
        this.plugin = plugin;
        loadBlacklistFromConfig();
    }

    private void loadBlacklistFromConfig() {
        // Expecting a config list like: blacklisted-items: ["DIAMOND_BLOCK", "TNT"]
        List<String> items = plugin.getConfig().getStringList("blacklisted-items");
        blacklistedMaterials.clear();
        for (String itemName : items) {
            try {
                Material mat = Material.valueOf(itemName.toUpperCase());
                blacklistedMaterials.add(mat);
            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Invalid blacklisted item in config: " + itemName);
            }
        }
    }

    private boolean isBlacklisted(ItemStack item) {
        if (item == null) return false;
        return blacklistedMaterials.contains(item.getType());
    }

    private void handle(Player player, ItemStack item) {
        plugin.handleIllegalItem(player, item);
    }

    // Check inventories when players join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (PermissionUtil.hasBypass(player)) return;
        for (ItemStack item : player.getInventory().getContents()) {
            if (isBlacklisted(item)) handle(player, item);
        }
    }

    // Check items when picked up
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (PermissionUtil.hasBypass(player)) return;

        ItemStack item = event.getItem().getItemStack();
        if (isBlacklisted(item)) {
            handle(player, item);
            event.setCancelled(true); // prevent pickup
        }
    }

    // Check items when moving in inventory
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (PermissionUtil.hasBypass(player)) return;

        ItemStack current = event.getCurrentItem();
        if (isBlacklisted(current)) {
            handle((Player) event.getWhoClicked(), current);
            event.setCancelled(true); // prevent move
        }
    }
}