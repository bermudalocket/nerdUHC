package com.bermudalocket.nerdUHC;

import java.util.List;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;

import org.bukkit.scoreboard.DisplaySlot;
//import org.bukkit.scoreboard.Team;

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
	
	List<String> rawteamlist = new ArrayList<String>();
	
	public Configuration(NerdUHC plugin) {
		this.plugin = plugin;
		plugin.saveDefaultConfig();
	}
	
	public void reload() {
		
		plugin.reloadConfig();
		
		String getgamemode = plugin.getConfig().getString("default-uhc-mode", "SOLO");
		DEFAULT_UHC_MODE = plugin.isValidGameMode(getgamemode) ? UHCGameMode.valueOf(getgamemode) : UHCGameMode.SOLO;
		
		plugin.getLogger().info("default_uhc_mode = " + DEFAULT_UHC_MODE.toString());
		
		HEALTH_DISPLAY_SLOT = DisplaySlot.valueOf(plugin.getConfig().getString("health-display-slot", "PLAYER_LIST"));
		KILLS_DISPLAY_SLOT = DisplaySlot.valueOf(plugin.getConfig().getString("health-display-slot", "SIDEBAR"));
		DISPLAY_HEALTH_BELOW_NAME = plugin.getConfig().getBoolean("display-health-below-name", true);
		
		LET_PLAYERS_PICK_TEAMS = plugin.getConfig().getBoolean("let-players-pick-teams", true);
		FORCE_EVEN_TEAMS = plugin.getConfig().getBoolean("force-even-teams", true);
		MAX_TEAM_SIZE = plugin.getConfig().getInt("max-team-size", 3);
		
		rawteamlist = plugin.getConfig().getStringList("teams");
		
	}

}
