package com.bermudalocket.nerdUHC;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.bermudalocket.nerdUHC.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.util.MatchMode;

public class Configuration {

    public static boolean DO_DEBUG;

	public static World WORLD;

	public static int WORLD_BORDER;

	public static int MATCH_DURATION;

	public static int MAX_TEAM_SIZE;

	public static MatchMode UHC_GAME_MODE;

	public static int PLAYER_COMBAT_TAG_TIME;

	public static EntityType COMBAT_TAG_DOPPEL;

	public static int SPAWN_BARRIER_RADIUS_SQUARED;

	public static boolean SPREAD_RESPECT_TEAMS;

	public static boolean ALLOW_FRIENDLY_FIRE;

	private static List<Map<?, ?>> GAMERULES = new ArrayList<>();

	public static List<Map<?, ?>> RAW_TEAM_LIST = new ArrayList<>();

	public static LinkedHashSet<String> RULES = new LinkedHashSet<>();

	public Configuration() {
		NerdUHC.PLUGIN.saveDefaultConfig();
		reload();
	}

	private void reload() {
		NerdUHC.PLUGIN.reloadConfig();
		FileConfiguration config = NerdUHC.PLUGIN.getConfig();

		DO_DEBUG = config.getBoolean("debug", false);

		WORLD = Bukkit.getWorld(config.getString("world-name", "world"));
		if (WORLD == null) NerdUHC.PLUGIN.getLogger().info(ChatColor.RED + "World specified in CONFIG is invalid, and a default world named \"world\" could not be found.");

		WORLD_BORDER = config.getInt("world-border", 2500);
		WORLD.getWorldBorder().setSize(WORLD_BORDER);

		int spawnX = config.getInt("spawn-x", 0);
		int spawnY = config.getInt("spawn-y", 65);
		int spawnZ = config.getInt("spawn-z", 0);
		WORLD.setSpawnLocation(new Location(WORLD, spawnX, spawnY, spawnZ));

		WORLD.setKeepSpawnInMemory(false);
		WORLD.getWorldBorder().setCenter(WORLD.getSpawnLocation());

		String tryMode = config.getString("uhc-game-mode", "SOLO");
		if (isValidGameMode(tryMode)) {
			UHC_GAME_MODE = MatchMode.valueOf(tryMode);
		} else {
			UHC_GAME_MODE = MatchMode.SOLO;
			NerdUHC.PLUGIN.getLogger().info(ChatColor.RED + "Invalid UHC Game Mode specified. Defaulting to SOLO.");
		}

		MATCH_DURATION = config.getInt("match-duration-in-minutes", 180);
		if (MATCH_DURATION == 0 || MATCH_DURATION < 0) {
			MATCH_DURATION = 180;
			NerdUHC.PLUGIN.getLogger().info(ChatColor.RED + "Invalid match duration specified. Must be an integer greater than 0. Defaulting to 180 minutes.");
		}

		int SPAWN_BARRIER_RADIUS = config.getInt("spawn-barrier-radius", 6);
		if (SPAWN_BARRIER_RADIUS < 0) {
			SPAWN_BARRIER_RADIUS = 6;
			NerdUHC.PLUGIN.getLogger().info(ChatColor.RED + "Spawn barrier radius is negative. Defaulting to 6.");
		}
		SPAWN_BARRIER_RADIUS_SQUARED = SPAWN_BARRIER_RADIUS * SPAWN_BARRIER_RADIUS;
		
		MAX_TEAM_SIZE = config.getInt("max-team-size", 3);
		if (MAX_TEAM_SIZE == 0 || MAX_TEAM_SIZE < 0) {
			MAX_TEAM_SIZE = 3;
			NerdUHC.PLUGIN.getLogger().info(ChatColor.RED + "Value of MAX_TEAM_SIZE is either 0 or negative! Must be a positive integer. Defaulting to 3.");
		}
		
		ALLOW_FRIENDLY_FIRE = config.getBoolean("allow-friendly-fire", false);

		PLAYER_COMBAT_TAG_TIME = config.getInt("player-combat-tag-time-in-sec", 30);
		if (PLAYER_COMBAT_TAG_TIME < 0) {
			NerdUHC.PLUGIN.getLogger().info(ChatColor.RED + "Value of PLAYER_COMBAT_TAG_TIME is invalid! Must be a positive integer or 0. Defaulting to 30.");
		}
		
		try {
			COMBAT_TAG_DOPPEL = EntityType.valueOf(config.getString("combat-tag-doppel"));
		} catch (Exception f) {
			COMBAT_TAG_DOPPEL = EntityType.CHICKEN;
			NerdUHC.PLUGIN.getLogger().info(ChatColor.RED + "Value of COMBAT_TAG_DOPPEL in CONFIG is invalid! Must be an ENTITYTYPE. Defaulting to CHICKEN. See Bukkit.EntityType for more info.");
		}
		
		SPREAD_RESPECT_TEAMS = config.getBoolean("spread-respect-teams", true);

		GAMERULES = config.getMapList("gamerules");

		BukkitRunnable setGameRulesTask = new BukkitRunnable() {
			@Override
			public void run() {
				GAMERULES.forEach(gamerule -> {
					String rule = gamerule.keySet().toArray()[0].toString();
					String value = gamerule.values().toArray()[0].toString();
					WORLD.setGameRuleValue(rule, value);
				});
			}
		};
		setGameRulesTask.runTaskLater(NerdUHC.PLUGIN, 1);
		
		RAW_TEAM_LIST = config.getMapList("teams");

		for (String string : config.getStringList("rules")) {
			RULES.add(ChatColor.translateAlternateColorCodes(Util.COLORCHAR, string));
		}
	}

	private boolean isValidGameMode(String gameMode) {
		try {
			MatchMode.valueOf(gameMode);
			return true;
		} catch (Exception f) {
			return false;
		}
	}

}
