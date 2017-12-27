package com.bermudalocket.nerdUHC;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.Material;

import com.bermudalocket.nerdUHC.NerdUHC.UHCGameMode;

public class Configuration {
	
	private NerdUHC plugin;
	
	public DisplaySlot HEALTH_DISPLAY_SLOT;
	public DisplaySlot KILLS_DISPLAY_SLOT;
	public boolean DISPLAY_HEALTH_BELOW_NAME;
	public boolean LET_PLAYERS_PICK_TEAMS;
	public boolean FORCE_EVEN_TEAMS;
	public int MAX_TEAM_SIZE;
	public UHCGameMode DEFAULT_UHC_MODE;
	public int PLAYER_COMBAT_TAG_TIME;
	public String SPAWN_BARRIER_BLOCK_NAME;
	public Material SPAWN_BARRIER_BLOCK;
	public int SPAWN_BARRIER_RADIUS;
	public int SPAWN_X;
	public int SPAWN_Y;
	public int SPAWN_Z;
	
	List<String> rawteamlist = new ArrayList<String>();
	
	public Configuration(NerdUHC plugin) {
		this.plugin = plugin;
		plugin.saveDefaultConfig();
	}
	
	public void reload() {
		
		plugin.reloadConfig();
		
		String getgamemode = plugin.getConfig().getString("default-uhc-mode", "SOLO");
		DEFAULT_UHC_MODE = plugin.isValidGameMode(getgamemode) ? UHCGameMode.valueOf(getgamemode) : UHCGameMode.SOLO;
		
		SPAWN_X = plugin.getConfig().getInt("spawn-x", 0);
		SPAWN_Y = plugin.getConfig().getInt("spawn-y", 65);
		SPAWN_Z = plugin.getConfig().getInt("spawn-z", 0);
		SPAWN_BARRIER_RADIUS = plugin.getConfig().getInt("spawn-barrier-radius", 6);
		SPAWN_BARRIER_BLOCK_NAME = plugin.getConfig().getString("spawn-barrier-block-id", "BARRIER");
		try {
			SPAWN_BARRIER_BLOCK = Material.valueOf(SPAWN_BARRIER_BLOCK_NAME);
		} catch (Exception f) {
			SPAWN_BARRIER_BLOCK = Material.BARRIER;
			plugin.throwError("Value of SPAWN_BARRIER_BLOCK_ID in config is invalid");
		} 
		
		HEALTH_DISPLAY_SLOT = DisplaySlot.valueOf(plugin.getConfig().getString("health-display-slot", "PLAYER_LIST"));
		KILLS_DISPLAY_SLOT = DisplaySlot.valueOf(plugin.getConfig().getString("health-display-slot", "SIDEBAR"));
		DISPLAY_HEALTH_BELOW_NAME = plugin.getConfig().getBoolean("display-health-below-name", true);
		
		LET_PLAYERS_PICK_TEAMS = plugin.getConfig().getBoolean("let-players-pick-teams", true);
		FORCE_EVEN_TEAMS = plugin.getConfig().getBoolean("force-even-teams", true);
		MAX_TEAM_SIZE = plugin.getConfig().getInt("max-team-size", 3);
		PLAYER_COMBAT_TAG_TIME = plugin.getConfig().getInt("player-combat-tag-time-in-sec", 30);
		
		rawteamlist = plugin.getConfig().getStringList("teams");
		
	}

}
