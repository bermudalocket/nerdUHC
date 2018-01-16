package com.bermudalocket.nerdUHC;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import com.bermudalocket.nerdUHC.modules.UHCGameMode;

import net.md_5.bungee.api.ChatColor;

/////////////////////////////////////////////////////////////////////////////
//
//	Config.yml configuration
//
//

public class Configuration {
	
	private NerdUHC plugin;
	
	public List<MatchHandler> matches;

	public boolean DEBUG;

	public int MATCH_DURATION;
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
	public int DEATHMATCH_DIST_BTWN_PLAYERS;
	public int DEATHMATCH_SPREAD_DIST_FROM_SPAWN;
	public boolean DO_DEATHMATCH;
	public boolean ALLOW_FRIENDLY_FIRE;
	
	List<Map<?, ?>> rawmatchlist = new ArrayList<Map<?, ?>>();
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
	
	public void reload() {
		
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();
		
		DEBUG = config.getBoolean("debug", false);
		
		String getgamemode = config.getString("default-uhc-mode", "SOLO");
		DEFAULT_UHC_MODE = isValidGameMode(getgamemode) ? UHCGameMode.valueOf(getgamemode) : UHCGameMode.SOLO;
		
		MATCH_DURATION = config.getInt("match-duration-in-minutes", 180);
		
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

		FORCE_EVEN_TEAMS = config.getBoolean("force-even-teams", true);
		MAX_TEAM_SIZE = config.getInt("max-team-size", 3);
		ALIVE_TEAM_NAME = config.getString("alive-team-name", "Alive");
		DEAD_TEAM_NAME = config.getString("dead-team-name", "Dead");
		ALLOW_FRIENDLY_FIRE = config.getBoolean("allow-friendly-fire", false);

		PLAYER_COMBAT_TAG_TIME = config.getInt("player-combat-tag-time-in-sec", 30);
		try {
			COMBAT_TAG_DOPPEL = EntityType.valueOf(config.getString("combat-tag-doppel"));
		} catch (Exception f) {
			COMBAT_TAG_DOPPEL = EntityType.CHICKEN;
			plugin.getLogger().info(ChatColor.RED + "Value of COMBAT_TAG_DOPPEL in config is invalid!");
		}

		SPREAD_DIST_BTWN_PLAYERS = config.getInt("spread-distance-between-players", 200);
		SPREAD_DIST_FROM_SPAWN = config.getDouble("spread-distance-from-spawn", 950);
		SPREAD_RESPECT_TEAMS = config.getBoolean("spread-respect-teams", true);
		
		DO_DEATHMATCH = config.getBoolean("do-deathmatch", true);
		DEATHMATCH_DIST_BTWN_PLAYERS = config.getInt("deathmatch-distance-between-players", 20);
		DEATHMATCH_SPREAD_DIST_FROM_SPAWN = config.getInt("deathmatch-distance-from-spawn", 100);

		GAMERULES = config.getMapList("gamerules");
		rawteamlist = config.getMapList("teams");
		rawobjectiveslist = config.getMapList("objectives");
		rawmatchlist = plugin.getConfig().getMapList("matches");
		
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
