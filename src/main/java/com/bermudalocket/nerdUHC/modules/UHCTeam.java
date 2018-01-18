package com.bermudalocket.nerdUHC.modules;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import com.bermudalocket.nerdUHC.match.MatchHandler;

public class UHCTeam {
	
	private MatchHandler match;
	private String name;
	private ChatColor color;
	private List<UHCPlayer> players;
	private boolean friendlyfire;
	private int maxplayers;
	
	public UHCTeam(MatchHandler match, String name, ChatColor color, int maxplayers, boolean friendlyfire) {
		this.match = match;
		this.name = name;
		this.color = color;
		this.maxplayers = maxplayers;
		this.friendlyfire = friendlyfire;
		this.players = new ArrayList<UHCPlayer>();
	}
	
	public void add(UHCPlayer p) {
		players.add(p);
	}
	
	public void remove(UHCPlayer p) {
		players.remove(p);
	}
	
	public MatchHandler getMatch() {
		return match;
	}
	
	public String getName() {
		return name;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public String getDisplayName() {
		return getColor() + getName() + ChatColor.WHITE;
	}
	
	public List<UHCPlayer> getPlayers() {
		return players;
	}
	
	public int getSize() {
		return players.size();
	}
	
	public int getMaxSize() {
		return maxplayers;
	}
	
	public boolean getFriendlyFire() {
		return friendlyfire;
	}
	
	public int getAlivePlayers() {
		int alive = 0;
		for (UHCPlayer p : players) {
			if (p.isAlive()) alive++;
		}
		return alive;
	}
	
	public boolean isFull() {
		return (getSize() >= maxplayers);
	}

}
