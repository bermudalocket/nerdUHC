package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.bermudalocket.nerdUHC.NerdUHC;

public class PreMatchListener implements Listener {

	private final NerdUHC plugin;
	
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
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (e.getTo().distanceSquared(plugin.CONFIG.WORLD.getSpawnLocation()) > plugin.CONFIG.SPAWN_BARRIER_RADIUS_SQUARED) {
			e.getPlayer().teleport(e.getFrom());
		}
	}

}
