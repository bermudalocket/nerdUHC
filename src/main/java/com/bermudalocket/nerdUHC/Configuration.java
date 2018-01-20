package com.bermudalocket.nerdUHC;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import com.bermudalocket.nerdUHC.modules.UHCGameMode;

import net.md_5.bungee.api.ChatColor;

public class Configuration {

	private NerdUHC plugin;

	public World WORLD;
	public int MATCH_DURATION;
	public boolean FORCE_EVEN_TEAMS;
	public String ALIVE_TEAM_NAME;
	public String DEAD_TEAM_NAME;
	public int MAX_TEAM_SIZE;
	public UHCGameMode UHC_GAME_MODE;
	public int PLAYER_COMBAT_TAG_TIME;
	public EntityType COMBAT_TAG_DOPPEL;
	public String SPAWN_BARRIER_BLOCK_NAME;
	public Material SPAWN_BARRIER_BLOCK;
	public int SPAWN_BARRIER_RADIUS;
	public Location SPAWN;
	public Location SPAWNFIXME;
	public int SPAWN_X;
	public int SPAWN_Y;
	public int SPAWN_Z;
	public int SPREAD_DIST_BTWN_PLAYERS;
	public double SPREAD_DIST_FROM_SPAWN;
	public boolean SPREAD_RESPECT_TEAMS;
	public List<Map<?, ?>> GAMERULES = new ArrayList<>();
	public String DEATH_OBJECTIVE_NAME;
	public int DEATHMATCH_DIST_BTWN_PLAYERS;
	public int DEATHMATCH_SPREAD_DIST_FROM_SPAWN;
	public boolean DO_DEATHMATCH;
	public boolean ALLOW_FRIENDLY_FIRE;

	List<Map<?, ?>> rawmatchlist = new ArrayList<Map<?, ?>>();
	List<Map<?, ?>> rawteamlist = new ArrayList<Map<?, ?>>();
	List<Map<?, ?>> rawobjectiveslist = new ArrayList<Map<?, ?>>();

	public Configuration(NerdUHC plugin) {
		this.plugin = plugin;
		plugin.saveDefaultConfig();
	}

	public void reload() {
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();

		WORLD = Bukkit.getWorld(config.getString("world-name", "world"));
		if (WORLD.equals(null)) plugin.getLogger().info(ChatColor.RED + "World specified in config is invalid, and a default world named \"world\" could not be found.");

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

		SPAWN_X = config.getInt("spawn-x", 0);
		SPAWN_Y = config.getInt("spawn-y", 65);
		SPAWN_Z = config.getInt("spawn-z", 0);
		SPAWN = new Location(WORLD, SPAWN_X, SPAWN_Y, SPAWN_Z);
		SPAWNFIXME = new Location(WORLD, SPAWN_X, 255, SPAWN_Z);
		if (SPAWN_X > WORLD.getWorldBorder().getSize()/2) {
			plugin.getLogger().info(ChatColor.RED + "SPAWN_X coordinate is outside world border. Are you sure?");
		}
		if (SPAWN_Y > 255 || SPAWN_Y < 0) {
			plugin.getLogger().info(ChatColor.RED + "SPAWN_Y coordinate is either above 255 or below 0. Are you sure?");
		}
		if (SPAWN_Z > WORLD.getWorldBorder().getSize()/2) {
			plugin.getLogger().info(ChatColor.RED + "SPAWN_Z coordinate is outside world border. Are you sure?");
		}
		
		SPAWN_BARRIER_RADIUS = config.getInt("spawn-barrier-radius", 6);
		if (SPAWN_BARRIER_RADIUS < 0) {
			SPAWN_BARRIER_RADIUS = 6;
			plugin.getLogger().info(ChatColor.RED + "Spawn barrier radius is negative. Defaulting to 6.");
		}
		
		SPAWN_BARRIER_BLOCK_NAME = config.getString("spawn-barrier-block-name", "BARRIER");
		try {
			SPAWN_BARRIER_BLOCK = Material.valueOf(SPAWN_BARRIER_BLOCK_NAME);
		} catch (Exception f) {
			SPAWN_BARRIER_BLOCK = Material.BARRIER;
			plugin.getLogger().info(ChatColor.RED + "Value of SPAWN_BARRIER_BLOCK_ID in config is invalid! Must be a MATERIAL. See Bukkit.Material for more info.");
		}

		FORCE_EVEN_TEAMS = config.getBoolean("force-even-teams", true);
		
		MAX_TEAM_SIZE = config.getInt("max-team-size", 3);
		if (MAX_TEAM_SIZE == 0 || MAX_TEAM_SIZE < 0) {
			MAX_TEAM_SIZE = 3;
			plugin.getLogger().info(ChatColor.RED + "Value of MAX_TEAM_SIZE is either 0 or negative! Must be a positive integer. Defaulting to 3.");
		}
		
		ALIVE_TEAM_NAME = config.getString("alive-team-name", "Alive");
		
		DEAD_TEAM_NAME = config.getString("dead-team-name", "Dead");
		
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

		SPREAD_DIST_BTWN_PLAYERS = config.getInt("spread-distance-between-players", 200);
		if (SPREAD_DIST_BTWN_PLAYERS == 0 || SPREAD_DIST_BTWN_PLAYERS < 0) {
			SPREAD_DIST_BTWN_PLAYERS = 200;
			plugin.getLogger().info(ChatColor.RED + "Value of SPREAD_DIST_BTWN_PLAYERS in config is invalid! Must be a positive integer. Defaulting to 200.");
		}
		
		SPREAD_DIST_FROM_SPAWN = config.getDouble("spread-distance-from-spawn", 950);
		if (SPREAD_DIST_FROM_SPAWN == 0 || SPREAD_DIST_FROM_SPAWN < 0) {
			SPREAD_DIST_FROM_SPAWN = 950;
			plugin.getLogger().info(ChatColor.RED + "Value of SPREAD_DIST_FROM_SPAWN in config is invalid! Must be a positive integer. Defaulting to 950.");
		}
		
		SPREAD_RESPECT_TEAMS = config.getBoolean("spread-respect-teams", true);

		DO_DEATHMATCH = config.getBoolean("do-deathmatch", true);
		
		DEATHMATCH_DIST_BTWN_PLAYERS = config.getInt("deathmatch-distance-between-players", 20);
		if (DEATHMATCH_DIST_BTWN_PLAYERS == 0 || DEATHMATCH_DIST_BTWN_PLAYERS < 0) {
			DEATHMATCH_DIST_BTWN_PLAYERS = 20;
			plugin.getLogger().info(ChatColor.RED + "Value of DEATHMATCH_DIST_BTWN_PLAYERS in config is invalid! Must be a positive integer. Defaulting to 20.");
		}
		
		DEATHMATCH_SPREAD_DIST_FROM_SPAWN = config.getInt("deathmatch-distance-from-spawn", 100);
		if (DEATHMATCH_SPREAD_DIST_FROM_SPAWN == 0 || DEATHMATCH_SPREAD_DIST_FROM_SPAWN < 0) {
			DEATHMATCH_SPREAD_DIST_FROM_SPAWN = 100;
			plugin.getLogger().info(ChatColor.RED + "Value of DEATHMATCH_SPREAD_DIST_FROM_SPAWN in config is invalid! Must be a positive integer. Defaulting to 100.");
		}
		
		GAMERULES = config.getMapList("gamerules");
		
		rawteamlist = config.getMapList("teams");
		
		rawobjectiveslist = config.getMapList("objectives");
	}

	public boolean isValidGameMode(String gameMode) {
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

	public List<Map<?, ?>> getRawObjectivesList() {
		return rawobjectiveslist;
	}

}
