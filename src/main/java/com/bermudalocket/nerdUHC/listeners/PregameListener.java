package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;

public class PregameListener implements Listener {

	private NerdUHC plugin;
	
	public PregameListener(NerdUHC plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent e) {
		UHCMatch currentmatch = plugin.matchHandler.getMatch();
		if (currentmatch.getMatchState() == UHCMatchState.PREGAME) e.setCancelled(true);
	}
}
