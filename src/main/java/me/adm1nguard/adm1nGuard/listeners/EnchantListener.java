package me.adm1nguard.adm1nGuard.listeners;

import me.adm1nguard.adm1nGuard.Adm1nGuard;
import me.adm1nguard.adm1nGuard.checkers.IllegalEnchantChecker;
import me.adm1nguard.adm1nGuard.utils.PermissionUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantListener implements Listener {

    private final Adm1nGuard plugin;

    public EnchantListener(Adm1nGuard plugin) {
        this.plugin = plugin;
    }

    // Check when a player joins
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (PermissionUtil.hasBypass(player)) return;
        for (ItemStack item : player.getInventory().getContents()) {
            if (IllegalEnchantChecker.hasIllegalEnchants(item)) {
                plugin.handleIllegalItem(player, item);
            }
        }
    }

    // Check when a player picks up an item
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (PermissionUtil.hasBypass(player)) return;
        ItemStack item = event.getItem().getItemStack();
        if (IllegalEnchantChecker.hasIllegalEnchants(item)) {
            plugin.handleIllegalItem(player, item);
            event.setCancelled(true); // prevent pickup
        }
    }

    // Check during inventory click (moving items)
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (PermissionUtil.hasBypass(player)) return;

        ItemStack current = event.getCurrentItem();
        if (IllegalEnchantChecker.hasIllegalEnchants(current)) {
            plugin.handleIllegalItem((Player) event.getWhoClicked(), current);
            event.setCancelled(true); // prevent move
        }
    }
}