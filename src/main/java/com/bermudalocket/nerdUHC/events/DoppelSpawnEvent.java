package com.bermudalocket.nerdUHC.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.bermudalocket.nerdUHC.modules.UHCDoppel;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;

public class DoppelSpawnEvent extends Event {
	
	private UHCPlayer player;
	private UHCDoppel doppel;
	private static final HandlerList handlers = new HandlerList();

	public DoppelSpawnEvent(UHCPlayer p, UHCDoppel d) {
		this.player = p;
		this.doppel = d;
	}
	
	public UHCPlayer getPlayer() {
		return player;
	}
	
	public UHCDoppel getDoppel() {
		return doppel;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
