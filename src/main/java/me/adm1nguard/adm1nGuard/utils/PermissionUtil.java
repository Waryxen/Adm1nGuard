package me.adm1nguard.adm1nGuard.utils;

import org.bukkit.entity.Player;

public class PermissionUtil {

    // The bypass permission constant
    private static final String BYPASS_PERMISSION = "adm1nguard.bypass";
    private static final String STAFF_PERMISSION = "adm1nguard.staff";

    /**
     * Checks if a player has the bypass permission.
     *
     * @param player The player to check
     * @return true if the player has bypass permission, false otherwise
     */
    public static boolean hasBypass(Player player) {
        if (player == null) return false;
        return player.hasPermission(BYPASS_PERMISSION);
    }

    /**
     * Checks if a player has the staff permission.
     *
     * @param player The player to check
     * @return true if the player has staff permission, false otherwise
     */
    public static boolean hasStaff(Player player) {
        if (player == null) return false;
        return player.hasPermission(STAFF_PERMISSION);
    }
}