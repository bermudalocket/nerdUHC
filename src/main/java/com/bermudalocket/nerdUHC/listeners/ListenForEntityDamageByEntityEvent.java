package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.bermudalocket.nerdUHC.NerdUHC;

/////////////////////////////////////////////////////////////////////////////
//
//	Listener: EntityDamageByEntityEvent
//  Executes: Combat tagging
//

public class ListenForEntityDamageByEntityEvent implements Listener {
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Listen
	//
	//
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerCombat(EntityDamageByEntityEvent e) {
		
		if (e.getEntity() == null || e.getDamager() == null) return;
		
		if ((e.getEntity() instanceof Player) || (e.getDamager() instanceof Player)) {
			NerdUHC.combatLogger.tagCombat(e.getEntity().getUniqueId(), e.getDamager().getUniqueId());
		}

	}
	
}
