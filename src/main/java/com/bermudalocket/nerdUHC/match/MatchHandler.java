package com.bermudalocket.nerdUHC.match;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCMatch;

public class MatchHandler {

	private NerdUHC plugin;
	private UHCMatch match;

	public MatchHandler(NerdUHC plugin) {
		this.plugin = plugin;
		getNewMatch();
	}
	
	public void getNewMatch() {
		match = new UHCMatch(plugin);
	}
	
	public UHCMatch getMatch() {
		return match;
	}
	
}
