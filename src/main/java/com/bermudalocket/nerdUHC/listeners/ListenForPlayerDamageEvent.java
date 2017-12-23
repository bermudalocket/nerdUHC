package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.bermudalocket.nerdUHC.nerdUHC;

public class ListenForPlayerDamageEvent implements Listener {
	
	public nerdUHC plugin;
	
	public Player player;
	
	public ListenForPlayerDamageEvent(nerdUHC plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerDamage(EntityDamageEvent e) {
		player = (Player) e.getEntity();
		
	}
	
	
}
