# Adm1nGuard

> ⚠ **Paper-only plugin**

**Adm1nGuard** is a lightweight PaperMC anti-illegal-item plugin that checks player items for:

* Illegal enchantments
* Illegal attribute modifiers
* Blacklisted items handled by your listener system

When a violation is found, the plugin can notify the player, alert staff, and remove the item.

## Requirements

- **Paper** server software
-
- A compatible **Minecraft/Paper API version**
- Java version required by your build setup

> This plugin is designed for **Paper servers** and will likely work on **Paper forks**, but it is **not guaranteed** to work on Spigot or Bukkit.

## Features

* Detects illegal enchantments on items
* Detects illegal attribute modifiers on items
* Sends staff alerts to players with permission
* Removes illegal items from player inventories
* Easy to extend with more checkers and listeners

## Permissions

* `adm1nguard.staff` — receives staff alerts when a player is caught with an illegal item

## Installation

1. Build the plugin JAR.
2. Place the JAR in your server's `plugins` folder.
3. Start or restart the server.
4. Make sure your server is running a compatible Spigot/Paper version.

## Usage

The plugin automatically checks items through its listeners.

Example flow:

1. A player obtains or uses an illegal item.
2. A listener detects it.
3. `handleIllegalItem(Player, ItemStack)` is called.
4. The player receives a warning.
5. Staff with the proper permission receive an alert.
6. The illegal item is removed.

## Notes

* This plugin currently focuses on validation and enforcement.

## License

Add your preferred license here.
