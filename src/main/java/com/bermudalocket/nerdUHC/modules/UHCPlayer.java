package com.bermudalocket.nerdUHC.modules;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UHCPlayer {
	
	private Player p;
	private UUID player;
	private UHCDoppel doppel;
	private String name;
	private UHCTeam team;
	private ChatColor color;
	private long combattag;
	private boolean alive;
	private boolean dropinventory;
	
	public UHCPlayer(UUID player) {
		this(player, null);
	}
	
	public UHCPlayer(UUID player, UHCTeam team) {
		this.player = player;
		this.p = Bukkit.getPlayer(player);
		this.name = p.getName();
		this.team = team;
		this.alive = true;
		this.combattag = 0;
		this.dropinventory = true;
		if (team != null) { 
			this.color = team.getColor();
		} else {
			this.color = ChatColor.WHITE;
		}
	}
	
	public void reset() {
		this.doppel = null;
		this.team = null;
		this.color = ChatColor.WHITE;
		this.combattag = 0;
		this.alive = true;
		this.dropinventory = true;
		setDisplayName();
	}
	
	public Player bukkitPlayer() {
		return Bukkit.getPlayer(player);
	}
	
	public void setDisplayName() {
		bukkitPlayer().setDisplayName(color + name);
		bukkitPlayer().setPlayerListName(color + name);
	}
	
	public String getDisplayName() {
		return color + name;
	}
	
	public void setTeam(UHCTeam team) {
		this.team = team;
		if (team != null) { 
			this.color = team.getColor();
			setDisplayName();
		}
	}
	
	public UHCTeam getTeam() {
		return team;
	}
	
	public boolean hasTeam() {
		return (team != null);
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
	
	public void setDoppel(UHCDoppel d) {
		this.doppel = d;
	}
	
	public UHCDoppel getDoppel() {
		return doppel;
	}
	
	public void setInvDrop(boolean state) {
		this.dropinventory = state;
	}
	
	public boolean dropInventory() {
		return this.dropinventory;
	}
	
	public boolean isDoppelDead() {
		return doppel.bukkitEntity().isDead();
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
