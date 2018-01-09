package com.bermudalocket.nerdUHC.modules;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;

public class Match {
	
	private NerdUHC plugin;
	
	public enum UHCGameMode {
		SOLO,
		TEAM
	}
	private UHCGameMode _mode;
	private boolean _inprogress = false;

	private World world;
	
	private HashMap<UUID, UHCPlayer> playerlist = new HashMap<UUID, UHCPlayer>();
	
	public Match(NerdUHC plugin) {
		this.plugin = plugin;
		
		world = Bukkit.getServer().getWorld("world");
		_mode = plugin.CONFIG.DEFAULT_UHC_MODE;
	}
	
	public void registerPlayer(UUID player, String team) {
		playerlist.put(player, new UHCPlayer(player, plugin));
		
		if (team.equals("SPECTATOR")) {
			Bukkit.getPlayer(player).setGameMode(GameMode.SPECTATOR);
		} else {
			playerlist.get(player).setTeam(team);
		}
	}
	
	public void unregisterPlayer(UUID player) {
		playerlist.remove(player);
		Bukkit.getPlayer(player).setGameMode(GameMode.SPECTATOR);
	}
	
	public boolean playerExists(UUID player) {
		return playerlist.containsKey(player);
	}
	
	public UHCPlayer getPlayer(UUID player) {
		return playerlist.containsKey(player) ? playerlist.get(player) : null;
	}
	
	public void setGameRules() {
		plugin.CONFIG.GAMERULES.forEach(gamerule -> {
			String rule = gamerule.keySet().toArray()[0].toString();
			String value = gamerule.values().toArray()[0].toString();
			world.setGameRuleValue(rule, value);
		});
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public void setGameMode(UHCGameMode mode) {
		_mode = mode;
	}
	
	public UHCGameMode getGameMode() {
		return _mode;
	}
	
	public  boolean isValidGameMode(String gameMode) {
		try {
			UHCGameMode.valueOf(gameMode);
			return true;
		} catch (Exception f) {
			return false;
		}
	}
	
	public  boolean isGameStarted() { 
		return _inprogress;
	}
	
	public  void setGameStarted(Boolean bool) {
		this._inprogress = bool;
	}

}
