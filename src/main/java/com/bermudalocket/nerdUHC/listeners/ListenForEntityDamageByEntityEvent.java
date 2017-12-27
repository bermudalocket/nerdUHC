package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.bermudalocket.nerdUHC.CombatLogger;
import com.bermudalocket.nerdUHC.NerdUHC;

public class ListenForEntityDamageByEntityEvent implements Listener {
	
	private NerdUHC plugin;
	private CombatLogger combatLogger;
	
	public ListenForEntityDamageByEntityEvent(NerdUHC plugin) {
		this.plugin = plugin;

	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerCombat(EntityDamageByEntityEvent e) {
		if ((e.getEntity() instanceof Player) && (e.getDamager() instanceof Player)) {
			combatLogger.tagCombat((Player) e.getEntity(), (Player) e.getDamager());
		}

	}
	
}
