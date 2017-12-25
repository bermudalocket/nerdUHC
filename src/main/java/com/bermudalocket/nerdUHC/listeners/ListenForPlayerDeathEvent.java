package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.bermudalocket.nerdUHC.NerdUHC;

public class ListenForPlayerDeathEvent implements Listener {
	
	private NerdUHC plugin;
	
	public ListenForPlayerDeathEvent(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent e) {

		plugin.handleDeath(e.getEntity());
		
	}
	
}
