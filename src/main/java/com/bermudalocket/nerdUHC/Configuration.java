package com.bermudalocket.nerdUHC;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import com.bermudalocket.nerdUHC.modules.Match.UHCGameMode;

import net.md_5.bungee.api.ChatColor;

/////////////////////////////////////////////////////////////////////////////
//
//	Config.yml configuration
//
//

public class Configuration {
	
	private NerdUHC plugin;
	
	public boolean DEBUG;

	public boolean LET_PLAYERS_PICK_TEAMS;
	public boolean FORCE_EVEN_TEAMS;
	public String ALIVE_TEAM_NAME;
	public String DEAD_TEAM_NAME;
	public int MAX_TEAM_SIZE;
	public UHCGameMode DEFAULT_UHC_MODE;
	public int PLAYER_COMBAT_TAG_TIME;
	public EntityType COMBAT_TAG_DOPPEL;
	public String SPAWN_BARRIER_BLOCK_NAME;
	public Material SPAWN_BARRIER_BLOCK;
	public int SPAWN_BARRIER_RADIUS;
	public int SPAWN_X;
	public int SPAWN_Y;
	public int SPAWN_Z;
	public int SPREAD_DIST_BTWN_PLAYERS;
	public double SPREAD_DIST_FROM_SPAWN;
	public boolean SPREAD_RESPECT_TEAMS;
	public List<Map<?, ?>> GAMERULES = new ArrayList<>();
	public String DEATH_OBJECTIVE_NAME;
	
	List<Map<?, ?>> rawteamlist = new ArrayList<Map<?, ?>>();
	List<Map<?, ?>> rawobjectiveslist = new ArrayList<Map<?, ?>>();
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Constructor
	//
	//
	
	public Configuration(NerdUHC plugin) {
		this.plugin = plugin;
		plugin.saveDefaultConfig();
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Reload config
	//
	//
	
	// ********************************************
	// converts raw gamerules to GAMERULES map and
	// then sets each gamerule
	// ********************************************
	
	public void reload() {
		
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();
		
		DEBUG = config.getBoolean("debug", false);
		
		// ********************************************
		// GAME MODE CONFIG
		// ********************************************
		String getgamemode = config.getString("default-uhc-mode", "SOLO");
		DEFAULT_UHC_MODE = plugin.match.isValidGameMode(getgamemode) ? UHCGameMode.valueOf(getgamemode) : UHCGameMode.SOLO;
		
		// ********************************************
		// SPAWNPOINT and BARRIER CONFIG
		// ********************************************
		SPAWN_X = config.getInt("spawn-x", 0);
		SPAWN_Y = config.getInt("spawn-y", 65);
		SPAWN_Z = config.getInt("spawn-z", 0);
		SPAWN_BARRIER_RADIUS = config.getInt("spawn-barrier-radius", 6);
		SPAWN_BARRIER_BLOCK_NAME = config.getString("spawn-barrier-block-name", "BARRIER");
		try {
			SPAWN_BARRIER_BLOCK = Material.valueOf(SPAWN_BARRIER_BLOCK_NAME);
		} catch (Exception f) {
			SPAWN_BARRIER_BLOCK = Material.BARRIER;
			plugin.getLogger().info(ChatColor.RED + "Value of SPAWN_BARRIER_BLOCK_ID in config is invalid");
		}
		
		// ********************************************
		// TEAMS CONFIG
		// ********************************************
		LET_PLAYERS_PICK_TEAMS = config.getBoolean("let-players-pick-teams", true);
		FORCE_EVEN_TEAMS = config.getBoolean("force-even-teams", true);
		MAX_TEAM_SIZE = config.getInt("max-team-size", 3);
		ALIVE_TEAM_NAME = config.getString("alive-team-name", "Alive");
		DEAD_TEAM_NAME = config.getString("dead-team-name", "Dead");
		
		// ********************************************
		// CombatLogging CONFIG
		// ********************************************
		PLAYER_COMBAT_TAG_TIME = config.getInt("player-combat-tag-time-in-sec", 30);
		try {
			COMBAT_TAG_DOPPEL = EntityType.valueOf(config.getString("combat-tag-doppel"));
		} catch (Exception f) {
			COMBAT_TAG_DOPPEL = EntityType.CHICKEN;
			plugin.getLogger().info(ChatColor.RED + "Value of COMBAT_TAG_DOPPEL in config is invalid!");
		}
		
		// ********************************************
		// spreadplayers CONFIG
		// ********************************************
		SPREAD_DIST_BTWN_PLAYERS = config.getInt("spread-distance-between-players", 200);
		SPREAD_DIST_FROM_SPAWN = config.getDouble("spread-distance-from-spawn", plugin.match.getWorld().getWorldBorder().getSize()/2);
		SPREAD_RESPECT_TEAMS = config.getBoolean("spread-respect-teams", true);
		
		// ********************************************
		// Get raw maps and lists
		// ********************************************
		GAMERULES = config.getMapList("gamerules");
		rawteamlist = config.getMapList("teams");
		rawobjectiveslist = config.getMapList("objectives");
	}

}
