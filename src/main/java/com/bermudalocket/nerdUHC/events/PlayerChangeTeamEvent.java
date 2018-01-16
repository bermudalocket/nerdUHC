package com.bermudalocket.nerdUHC.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.bermudalocket.nerdUHC.modules.UHCPlayer;
import com.bermudalocket.nerdUHC.modules.UHCTeam;

public class PlayerChangeTeamEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private UHCPlayer p;
	private UHCTeam t;
	private UHCTeam oldteam;
	
	public PlayerChangeTeamEvent(UHCPlayer player, UHCTeam team) {
		this.p = player;
		this.t = team;
		if (player.getTeam() != null) {
			this.oldteam = player.getTeam();
		} else {
			this.oldteam = null;
		}
	}

	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean state) {
		this.cancelled = state;
	}
	
	public UHCPlayer getPlayer() {
		return p;
	}
	
	public UHCTeam getTeam() {
		return t;
	}
	
	public UHCTeam getOldTeam() {
		return oldteam;
	}
	
}
