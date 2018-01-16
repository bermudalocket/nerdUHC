package com.bermudalocket.nerdUHC.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.bermudalocket.nerdUHC.modules.UHCMatchState;

public class MatchStateChangeEvent extends Event {
	
	private UHCMatchState state = null;
	private UHCMatchState laststate = null;

	public MatchStateChangeEvent(UHCMatchState state) {
		if (state.equals(UHCMatchState.LAST)) {
			UHCMatchState placeholder = laststate;
			this.laststate = state;
			this.state = placeholder;
		} else {
			this.laststate = this.state;
			this.state = state;
		}
	}
	
	public UHCMatchState getState() {
		return this.state;
	}
	
	public UHCMatchState getLastState() {
		return this.laststate;
	}
	
	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
