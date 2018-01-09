package com.bermudalocket.nerdUHC.modules;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;

public class UHCPlayer {
	
	private NerdUHC plugin;
	
	private Player p;
	
	private UUID _player;
	private UUID _doppel;
	private String _name;
	private String _team;
	private ChatColor _color;
	private long _combattag = 0;
	private boolean _alive;
	
	public UHCPlayer(UUID player, NerdUHC plugin) {
		this._player = player;
		this._name = Bukkit.getPlayer(player).getName();
	}
	
	public void unite() {
		plugin.scoreboardHandler.setPlayerBoard(_player);
		p.setDisplayName(_color + p.getName() + ChatColor.WHITE);
		p.setPlayerListName(_color + p.getName() + ChatColor.WHITE);
	}
	
	public Player bukkitPlayer() {
		return p;
	}
	
	public void setTeam(String team) {
		if (team == null) {
			plugin.scoreboardHandler.removePlayerTeam(p);
		} else if (team.equalsIgnoreCase("SPECTATOR")) {
			_team = team;
			_color = ChatColor.ITALIC;
		} else {
			plugin.scoreboardHandler.setPlayerTeam(_player, team);
			_team = team;
			_color = plugin.scoreboardHandler.getTeamColor(team);
		}
		unite();
	}
	
	public String getTeam() {
		return _team;
	}
	
	public void setColor(ChatColor color) {
		this._color = color;
	}
	
	public ChatColor getColor() {
		return _color;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setCombatTag(long combattag) {
		this._combattag = combattag;
	}
	
	public long getCombatTag() {
		return this._combattag;
	}
	
	public void setDoppel(UUID doppel) {
		_doppel = doppel;
	}
	
	public UUID getDoppel() {
		return _doppel;
	}
	
	public void kill() {
		bukkitPlayer().damage(200.0);
	}
	
	public void setAlive(boolean state) {
		_alive = state;
	}
	
	public boolean isDead() {
		return !_alive;
	}

}
