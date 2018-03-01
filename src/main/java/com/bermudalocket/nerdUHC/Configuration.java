package com.bermudalocket.nerdUHC;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.modules.UHCGameMode;

public class Configuration {

	private final NerdUHC plugin;

	public World WORLD;
	public int MATCH_DURATION;
	public int MAX_TEAM_SIZE;
	public UHCGameMode UHC_GAME_MODE;
	public int PLAYER_COMBAT_TAG_TIME;
	public EntityType COMBAT_TAG_DOPPEL;
	public int SPAWN_BARRIER_RADIUS_SQUARED;
	public boolean SPREAD_RESPECT_TEAMS;
	private List<Map<?, ?>> GAMERULES = new ArrayList<>();
	public boolean ALLOW_FRIENDLY_FIRE;

	private List<Map<?, ?>> rawteamlist = new ArrayList<>();

	public Configuration(NerdUHC plugin) {
		this.plugin = plugin;
		plugin.saveDefaultConfig();
	}

	public void reload() {
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();

		WORLD = Bukkit.getWorld(config.getString("world-name", "world"));
		if (WORLD == null) plugin.getLogger().info(ChatColor.RED + "World specified in config is invalid, and a default world named \"world\" could not be found.");
		WORLD.setDifficulty(Difficulty.HARD);

		String getgamemode = config.getString("uhc-game-mode", "SOLO");
		if (isValidGameMode(getgamemode)) {
			UHC_GAME_MODE = UHCGameMode.valueOf(getgamemode);
		} else {
			UHC_GAME_MODE = UHCGameMode.SOLO;
			plugin.getLogger().info(ChatColor.RED + "Invalid UHC Game Mode specified. Defaulting to SOLO.");
		}

		MATCH_DURATION = config.getInt("match-duration-in-minutes", 180);
		if (MATCH_DURATION == 0 || MATCH_DURATION < 0) {
			MATCH_DURATION = 180;
			plugin.getLogger().info(ChatColor.RED + "Invalid match duration specified. Must be an integer greater than 0. Defaulting to 180 minutes.");
		}

		int SPAWN_X = config.getInt("spawn-x", 0);
		int SPAWN_Y = config.getInt("spawn-y", 65);
		int SPAWN_Z = config.getInt("spawn-z", 0);
		if (SPAWN_X > WORLD.getWorldBorder().getSize()/2) {
			plugin.getLogger().info(ChatColor.RED + "SPAWN_X coordinate is outside world border. Are you sure?");
		}
		if (SPAWN_Y > 255 || SPAWN_Y < 0) {
			plugin.getLogger().info(ChatColor.RED + "SPAWN_Y coordinate is either above 255 or below 0. Are you sure?");
		}
		if (SPAWN_Z > WORLD.getWorldBorder().getSize()/2) {
			plugin.getLogger().info(ChatColor.RED + "SPAWN_Z coordinate is outside world border. Are you sure?");
		}
		
		WORLD.setSpawnLocation(SPAWN_X, SPAWN_Y, SPAWN_Z);

		int SPAWN_BARRIER_RADIUS = config.getInt("spawn-barrier-radius", 6);
		if (SPAWN_BARRIER_RADIUS < 0) {
			SPAWN_BARRIER_RADIUS = 6;
			plugin.getLogger().info(ChatColor.RED + "Spawn barrier radius is negative. Defaulting to 6.");
		}
		SPAWN_BARRIER_RADIUS_SQUARED = SPAWN_BARRIER_RADIUS * SPAWN_BARRIER_RADIUS;
		
		MAX_TEAM_SIZE = config.getInt("max-team-size", 3);
		if (MAX_TEAM_SIZE == 0 || MAX_TEAM_SIZE < 0) {
			MAX_TEAM_SIZE = 3;
			plugin.getLogger().info(ChatColor.RED + "Value of MAX_TEAM_SIZE is either 0 or negative! Must be a positive integer. Defaulting to 3.");
		}
		
		ALLOW_FRIENDLY_FIRE = config.getBoolean("allow-friendly-fire", false);

		PLAYER_COMBAT_TAG_TIME = config.getInt("player-combat-tag-time-in-sec", 30);
		if (PLAYER_COMBAT_TAG_TIME < 0) {
			plugin.getLogger().info(ChatColor.RED + "Value of PLAYER_COMBAT_TAG_TIME is invalid! Must be a positive integer or 0. Defaulting to 30.");
		}
		
		try {
			COMBAT_TAG_DOPPEL = EntityType.valueOf(config.getString("combat-tag-doppel"));
		} catch (Exception f) {
			COMBAT_TAG_DOPPEL = EntityType.CHICKEN;
			plugin.getLogger().info(ChatColor.RED + "Value of COMBAT_TAG_DOPPEL in config is invalid! Must be an ENTITYTYPE. Defaulting to CHICKEN. See Bukkit.EntityType for more info.");
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
					plugin.getLogger().info("CONFIG: game rule " + rule + " set to value " + value);
				});
			}
		};
		setGameRulesTask.runTaskLater(plugin, 1);
		
		rawteamlist = config.getMapList("teams");
	}

	private boolean isValidGameMode(String gameMode) {
		try {
			UHCGameMode.valueOf(gameMode);
			return true;
		} catch (Exception f) {
			return false;
		}
	}

	public List<Map<?, ?>> getRawTeamList() {
		return rawteamlist;
	}

}
