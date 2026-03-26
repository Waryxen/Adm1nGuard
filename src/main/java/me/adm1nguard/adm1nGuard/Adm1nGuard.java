package me.adm1nguard.adm1nGuard;

import me.adm1nguard.adm1nGuard.checkers.IllegalEnchantChecker;
import me.adm1nguard.adm1nGuard.listeners.AttributeListener;
import me.adm1nguard.adm1nGuard.listeners.BlacklistListener;
import me.adm1nguard.adm1nGuard.listeners.EnchantListener;
import me.adm1nguard.adm1nGuard.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public final class Adm1nGuard extends JavaPlugin {

    private static Adm1nGuard instance;

    @Override
    public void onEnable() {
        instance = this;
        register(this, new EnchantListener(this));
        register(this, new AttributeListener(this));
        register(this, new BlacklistListener(this));
    }

    public static void register(JavaPlugin plugin, Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Adm1nGuard getInstance() {
        return instance;
    }

    // Handle illegal item
    public void handleIllegalItem(Player player, ItemStack item) {
        // Get illegal enchantments
        Map<Enchantment, Integer> illegal = IllegalEnchantChecker.getIllegalEnchants(item);

        if (illegal.isEmpty()) return;

        // Build a readable string of illegal enchants
        StringBuilder enchantsString = new StringBuilder();
        illegal.forEach((enchant, level) -> enchantsString.append("&c")
                .append(enchant.getKey().getKey())
                .append(" &6")
                .append(level)
                .append("&c, "));

        // Remove trailing comma and space
        if (enchantsString.length() >= 2) {
            enchantsString.setLength(enchantsString.length() - 2);
        }

        MessageUtils.sendMessage(player, "&cIllegal enchantments detected: " + enchantsString);

        String staffMessage = "&e[Staff Alert] &c" + player.getName() + " had illegal item: " + enchantsString;
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("adm1nguard.staff"))
                .forEach(p -> MessageUtils.sendMessage(p, staffMessage));

        // Remove illegal item
        player.getInventory().remove(item);

        getLogger().warning(player.getName() + " had illegal item: " + illegal);
    }
}
