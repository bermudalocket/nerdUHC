package com.bermudalocket.nerdUHC.modules;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UHCPlayer {
	
	private Player p;
	
	private UUID player;
	private UUID doppel;
	private String name;
	private UHCTeam team;
	private ChatColor color;
	private long combattag = 0;
	private boolean alive = true;
	private boolean doppeldeath = false;
	
	public UHCPlayer(UUID player) {
		this(player, null);
	}
	
	public UHCPlayer(UUID player, UHCTeam team) {
		this.player = player;
		this.p = Bukkit.getPlayer(player);
		this.name = p.getName();
		this.team = team;
		if (team != null) this.color = team.getColor();
	}
	
	public Player bukkitPlayer() {
		return Bukkit.getPlayer(player);
	}
	
	public void setTeam(UHCTeam team) {
		this.team = team;
	}
	
	public UHCTeam getTeam() {
		return team;
	}
	
	public boolean hasTeam() {
		return (team != null);
	}
	
	public void setColor(ChatColor color) {
		this.color = color;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public String getName() {
		return name;
	}
	
	public void setCombatTag(long combattag) {
		this.combattag = combattag;
	}
	
	public long getCombatTag() {
		return this.combattag;
	}
	
	public void setDoppel(UUID doppel) {
		this.doppel = doppel;
	}
	
	public UUID getDoppel() {
		return doppel;
	}
	
	public void setDoppelDeath(boolean state) {
		doppeldeath = state;
	}
	
	public boolean isDoppelDeath() {
		return doppeldeath;
	}
	
	public void setAlive(boolean state) {
		alive = state;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public void kill() {
		bukkitPlayer().setHealth(bukkitPlayer().getHealth() - bukkitPlayer().getHealth());
	}
	
	public boolean isDead() {
		return bukkitPlayer().isDead();
	}

}
