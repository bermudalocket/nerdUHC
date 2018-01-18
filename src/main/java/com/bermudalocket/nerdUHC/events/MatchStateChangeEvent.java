package com.bermudalocket.nerdUHC.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.bermudalocket.nerdUHC.modules.UHCMatchState;

public class MatchStateChangeEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private UHCMatchState state = null;
	private UHCMatchState laststate = null;

	public MatchStateChangeEvent(UHCMatchState state) {
		if (state.equals(UHCMatchState.LAST)) {
			UHCMatchState placeholder = laststate;
			this.laststate = state;
			this.state = placeholder;
		} else {
			if (this.state == null) {
				this.laststate = UHCMatchState.PREGAME;
				this.state = state;
			} else {
				this.laststate = this.state;
				this.state = state;
			}
		}
	}
	
	public UHCMatchState getState() {
		return this.state;
	}
	
	public UHCMatchState getLastState() {
		return this.laststate;
	}
	
	public boolean isTransitioning() {
		return (this.state.equals(UHCMatchState.TRANSITION));
	}
	
	public boolean hasTransitioned() {
		return (this.laststate.equals(UHCMatchState.TRANSITION));
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
