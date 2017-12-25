package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.bermudalocket.nerdUHC.NerdUHC;

public class ListenForPlayerJoinEvent implements Listener {
	
	private NerdUHC plugin;
	
	public ListenForPlayerJoinEvent(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent e) {
		plugin.registerPlayer(e.getPlayer(), false);		// true = ignore "has UHC started" check
	}
	
}
