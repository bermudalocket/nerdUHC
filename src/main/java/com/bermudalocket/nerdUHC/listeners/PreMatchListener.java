package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;

public class PreMatchListener implements Listener {

	private NerdUHC plugin;
	
	public PreMatchListener(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) e.setCancelled(true);
	}

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent e) {
		UHCMatch currentmatch = plugin.matchHandler.getMatch();
		if (currentmatch.getMatchState() == UHCMatchState.PREGAME) e.setCancelled(true);
	}
}
