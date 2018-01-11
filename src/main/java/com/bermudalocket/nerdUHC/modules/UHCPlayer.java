package com.bermudalocket.nerdUHC.modules;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
	private boolean _doppeldeath = false;
	
	public UHCPlayer(UUID player, NerdUHC plugin) {
		this._player = player;
		this.p = Bukkit.getPlayer(player);
		this._name = p.getName();
		this.plugin = plugin;
	}
	
	public void unite() {
		p = Bukkit.getPlayer(_player);
		plugin.scoreboardHandler.setPlayerBoard(_player);
		p.setDisplayName(_color + _name + ChatColor.WHITE);
		p.setPlayerListName(_color + _name + ChatColor.WHITE);
		if (_team.equalsIgnoreCase("SPECTATOR")) {
			p.setGameMode(GameMode.SPECTATOR);
		}
	}
	
	public Player bukkitPlayer() {
		return Bukkit.getPlayer(_player);
	}
	
	public void setTeam(String team) {
		if (team == null) {
			plugin.scoreboardHandler.removePlayerTeam(p);
			_team = null;
			_color = ChatColor.WHITE;
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
	
	public void setDoppelDeath(boolean state) {
		_doppeldeath = state;
	}
	
	public boolean isDoppelDeath() {
		return _doppeldeath;
	}
	
	public void setAlive(boolean state) {
		_alive = state;
	}
	
	public boolean isAlive() {
		return _alive;
	}
	
	public void kill() {
		bukkitPlayer().setHealth(bukkitPlayer().getHealth() - bukkitPlayer().getHealth());
	}
	
	public boolean isDead() {
		return bukkitPlayer().isDead();
	}

}
