package com.bermudalocket.nerdUHC.gui;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

class GUIStatic {

    static final Inventory TEAM_GUI = Bukkit.createInventory(null, 27, "Select a team:");
    static final ItemStack TEAM_RANDOM = createItem(Material.CHEST, "Random", null);

    static final Inventory DURATION_GUI = Bukkit.createInventory(null, 9, "Set match duration:");
    static final ItemStack DURATION_2 = createItem(Material.BEETROOT, "2 minutes (debug)", null);
    static final ItemStack DURATION_30 = createItem(Material.COAL, "30 minutes", null);
    static final ItemStack DURATION_60 = createItem(Material.IRON_INGOT, "60 minutes", null);
    static final ItemStack DURATION_120 = createItem(Material.GOLD_INGOT, "2 hours", null);
    static final ItemStack DURATION_180 = createItem(Material.DIAMOND, "3 hours", null);

    static final Inventory DIFFICULTY_GUI = Bukkit.createInventory(null, 9, "Set match difficulty:");
    static final ItemStack DIFFICULTY_PEACEFUL = createItem(new Wool(DyeColor.LIGHT_BLUE).toItemStack(1), "Peaceful", null);
    static final ItemStack DIFFICULTY_EASY = createItem(new Wool(DyeColor.LIME).toItemStack(1), "Easy", null);
    static final ItemStack DIFFICULTY_NORMAL = createItem(new Wool(DyeColor.ORANGE).toItemStack(1), "Normal", null);
    static final ItemStack DIFFICULTY_HARD = createItem(new Wool(DyeColor.RED).toItemStack(1), "Hard", null);

    static final Inventory FRIENDLY_FIRE_GUI = Bukkit.createInventory(null, 9, "Set friendly fire option:");
    static final ItemStack FRIENDLY_FIRE_ON = createItem(Material.DIAMOND_SWORD, "On", null);
    static final ItemStack FRIENDLY_FIRE_OFF = createItem(Material.SHIELD, "Off", null);

    static final ItemStack MENU_JOIN_TEAM = createItem(Material.STONE_SWORD, "Join A Team");
    static final ItemStack MENU_SPECTATE = createItem(Material.EYE_OF_ENDER, "Spectate");
    static final ItemStack MENU_INFO_BOOK = createItem(Material.WRITTEN_BOOK, "Help");
    static final ItemStack MENU_DURATION = createItem(Material.WATCH, "Duration");
    static final ItemStack MENU_DIFFICULTY = createItem(Material.DIAMOND_SWORD, "Difficulty");
    static final ItemStack MENU_FRIENDLY_FIRE = createItem(Material.BARRIER, "Friendly Fire");
    static final ItemStack MENU_START_MATCH = createItem(Material.NETHER_STAR, "Start");

    private static ItemStack createItem(Material material, String itemName) {
        return createItem(material, itemName, Enchantment.ARROW_KNOCKBACK);
    }

    private static ItemStack createItem(Material material, String itemName, Enchantment enchantment) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemName);
        itemStack.setItemMeta(itemMeta);
        if (enchantment != null) itemStack.addUnsafeEnchantment(enchantment, 10);
        return itemStack;
    }

    private static ItemStack createItem(ItemStack itemStack, String itemName, Enchantment enchantment) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemName);
        itemStack.setItemMeta(itemMeta);
        if (enchantment != null) itemStack.addUnsafeEnchantment(enchantment, 10);
        return itemStack;
    }

    static {
        DURATION_GUI.addItem(DURATION_2);
        DURATION_GUI.addItem(DURATION_30);
        DURATION_GUI.addItem(DURATION_60);
        DURATION_GUI.addItem(DURATION_120);
        DURATION_GUI.addItem(DURATION_180);

        DIFFICULTY_GUI.addItem(DIFFICULTY_PEACEFUL);
        DIFFICULTY_GUI.addItem(DIFFICULTY_EASY);
        DIFFICULTY_GUI.addItem(DIFFICULTY_NORMAL);
        DIFFICULTY_GUI.addItem(DIFFICULTY_HARD);

        FRIENDLY_FIRE_GUI.addItem(FRIENDLY_FIRE_ON);
        FRIENDLY_FIRE_GUI.addItem(FRIENDLY_FIRE_OFF);
    }

}
