package me.adm1nguard.adm1nGuard.listeners;

import me.adm1nguard.adm1nGuard.Adm1nGuard;
import me.adm1nguard.adm1nGuard.checkers.IllegalAttributeChecker;
import me.adm1nguard.adm1nGuard.checkers.IllegalEnchantChecker;
import me.adm1nguard.adm1nGuard.utils.KeyUtil;
import me.adm1nguard.adm1nGuard.utils.PermissionUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

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
                blacklistedMaterials.add(Material.valueOf(itemName.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Invalid blacklisted item in config: " + itemName);
            }
        }
    }

    private List<String> getReasons(ItemStack item) {
        List<String> reasons = new ArrayList<>();
        if (item == null || item.getType().isAir()) return reasons;

        if (blacklistedMaterials.contains(item.getType())) {
            reasons.add("Blacklisted Item: " + KeyUtil.material(item.getType()));
        }

        Map<Enchantment, Integer> illegalEnchants = IllegalEnchantChecker.getIllegalEnchants(item);
        illegalEnchants.forEach((enchant, level) ->
                reasons.add("Illegal Enchant: " + KeyUtil.key(enchant) + " " + level)
        );

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
        if (item == null || item.getType().isAir() || reasons.isEmpty()) return;
        plugin.handleIllegalItem(player, item, reasons);
    }

    private void scanContainer(Player player, ItemStack container) {
        if (container == null || container.getType().isAir()) return;

        ItemMeta meta = container.getItemMeta();
        if (!(meta instanceof BlockStateMeta blockStateMeta)) return;

        if (blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
            for (ItemStack item : shulkerBox.getInventory().getContents()) {
                if (item == null || item.getType().isAir()) continue;

                List<String> reasons = getReasons(item);
                if (!reasons.isEmpty()) {
                    handle(player, item, reasons);
                }

                // Recursively scan nested containers
                scanContainer(player, item);
            }
        }
    }

    private void scanInventory(Player player) {
        if (PermissionUtil.hasBypass(player)) return;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            List<String> reasons = getReasons(item);
            if (!reasons.isEmpty()) handle(player, item, reasons);
            scanContainer(player, item);
        }

        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor == null) continue;
            List<String> reasons = getReasons(armor);
            if (!reasons.isEmpty()) handle(player, armor, reasons);
            scanContainer(player, armor);
        }

        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (offhand != null) {
            List<String> offhandReasons = getReasons(offhand);
            if (!offhandReasons.isEmpty()) handle(player, offhand, offhandReasons);
            scanContainer(player, offhand);
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
        List<String> currentReasons = getReasons(current);

        if (!currentReasons.isEmpty()) {
            handle(player, current, currentReasons);
            event.setCancelled(true);
            return;
        }

        ItemStack cursor = event.getCursor();
        List<String> cursorReasons = getReasons(cursor);

        if (!cursorReasons.isEmpty()) {
            handle(player, cursor, cursorReasons);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (PermissionUtil.hasBypass(player)) return;

        for (ItemStack item : event.getInventory().getContents()) {
            if (item == null) continue;
            List<String> reasons = getReasons(item);
            if (!reasons.isEmpty()) {
                handle(player, item, reasons);
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryCreative(InventoryCreativeEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (PermissionUtil.hasBypass(player)) return;

        ItemStack current = event.getCurrentItem();
        List<String> reasons = getReasons(current);

        if (!reasons.isEmpty()) {
            handle(player, current, reasons);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (PermissionUtil.hasBypass(player)) return;

        ItemStack item = event.getItemDrop().getItemStack();
        List<String> reasons = getReasons(item);

        if (!reasons.isEmpty()) {
            handle(player, item, reasons);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (PermissionUtil.hasBypass(player)) return;

        ItemStack result = event.getInventory().getResult();
        List<String> reasons = getReasons(result);

        if (!reasons.isEmpty()) {
            handle(player, result, reasons);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        Player player = event.getPlayer();
        if (PermissionUtil.hasBypass(player)) return;

        ItemStack item = event.getItemStack();
        List<String> reasons = getReasons(item);

        if (!reasons.isEmpty()) {
            handle(player, item, reasons);
            player.getInventory().removeItem(item);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (PermissionUtil.hasBypass(player)) return;

        Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            List<String> reasons = getReasons(item);
            if (!reasons.isEmpty()) {
                handle(player, item, reasons);
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        scanInventory(player);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (PermissionUtil.hasBypass(player)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        List<String> reasons = getReasons(item);

        if (!reasons.isEmpty()) {
            handle(player, item, reasons);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!(event.getView().getPlayer() instanceof Player player)) return;
        if (PermissionUtil.hasBypass(player)) return;

        ItemStack result = event.getResult();
        List<String> reasons = getReasons(result);

        if (!reasons.isEmpty()) {
            handle(player, result, reasons);
            event.setResult(null);
        }
    }
}