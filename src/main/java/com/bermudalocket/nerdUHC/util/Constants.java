package com.bermudalocket.nerdUHC.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import java.util.ArrayList;
import java.util.Arrays;

public class Constants {

	public static final String HEART = ChatColor.RED + "❤";

	public static final String LOBBY = String.format("%sNerdUHC%s - %s%sLOBBY",
			ChatColor.BOLD, ChatColor.RESET, ChatColor.AQUA, ChatColor.ITALIC);

	public static final ArrayList<Biome> excludedBiomes = new ArrayList<>(Arrays.asList(
			Biome.OCEAN, Biome.DEEP_OCEAN, Biome.FROZEN_OCEAN));

	public static final ArrayList<Material> excludedBlocks = new ArrayList<>(Arrays.asList(
			Material.LAVA, Material.STATIONARY_LAVA, Material.WATER, Material.STATIONARY_WATER,
			Material.LEAVES, Material.LEAVES_2, Material.CACTUS));

	public static final String RESET_COLOR = String.format("%s%s", ChatColor.RESET, ChatColor.WHITE);

}
