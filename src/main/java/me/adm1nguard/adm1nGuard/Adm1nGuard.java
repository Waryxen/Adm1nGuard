package me.adm1nguard.adm1nGuard;

import me.adm1nguard.adm1nGuard.listeners.ItemSafetyListener;
import me.adm1nguard.adm1nGuard.utils.ColorUtils;
import me.adm1nguard.adm1nGuard.utils.KeyUtil;
import me.adm1nguard.adm1nGuard.utils.MessageUtils;
import me.adm1nguard.adm1nGuard.utils.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

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
        if (player == null || item == null || item.getType().isAir()) return;
        if (reasons == null || reasons.isEmpty()) return;

        // Remove duplicates while preserving order
        List<String> violations = new ArrayList<>(new LinkedHashSet<>(reasons));

        String violationsString = String.join("&c, ", violations);

        // Notify player
        MessageUtils.sendMessage(player, "&cIllegal item detected: " + violationsString);

        // Notify staff
        String staffMessage = "&e[Staff Alert] &c" + player.getName()
                + " had illegal item: " + violationsString;

        Bukkit.getOnlinePlayers().stream()
                .filter(PermissionUtil::hasStaff)
                .forEach(p -> MessageUtils.sendMessage(p, staffMessage));

        // Remove only one matching item safely
        player.getInventory().removeItemAnySlot(item);
        player.updateInventory();

        // Console log
        getLogger().warning(player.getName() + " had illegal item: "
                + KeyUtil.material(item.getType())
                + " -> " + ColorUtils.stripColor(violationsString.replace("&", "§")));
    }
}
