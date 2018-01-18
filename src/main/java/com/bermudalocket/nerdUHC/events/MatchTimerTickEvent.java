package com.bermudalocket.nerdUHC.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchTimerTickEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	public boolean cancelled;

	public MatchTimerTickEvent() {}
	
	public void cancel() {
		this.cancelled = true;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
