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
	private String displayname;
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
		if (team != null) this.color = team.getColor();
	}
	
	public Player bukkitPlayer() {
		return Bukkit.getPlayer(player);
	}
	
	public void setDisplayName() {
		if (team != null) {
			this.displayname = team.getColor() + name + ChatColor.RESET;
		} else {
			this.displayname = this.name;
		}
	}
	
	public String getDisplayName() {
		return this.displayname;
	}
	
	public void setTeam(UHCTeam team) {
		this.team = team;
		if (team != null) this.color = team.getColor();
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
