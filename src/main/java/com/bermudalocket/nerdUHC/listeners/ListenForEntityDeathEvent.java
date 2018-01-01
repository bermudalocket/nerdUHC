package com.bermudalocket.nerdUHC.listeners;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

import com.bermudalocket.nerdUHC.NerdUHC;

/////////////////////////////////////////////////////////////////////////////
//
//	Listener: PlayerDeathEvent
//  Executes: calls method which resets score, removes from team, etc
//

public class ListenForEntityDeathEvent implements Listener {
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Listen
	//
	//
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onEntityDeath(EntityDeathEvent e) {
		
		if (e.getEntity().getType().equals(NerdUHC.CONFIG.COMBAT_TAG_DOPPEL)) {
			
			UUID doppel = e.getEntity().getUniqueId();

			if (NerdUHC.combatLogger.isDoppel(doppel) && 
					(e.getEntity().getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK) ||
					 e.getEntity().getLastDamageCause().getCause().equals(DamageCause.FIRE_TICK))) {
				e.getDrops().clear();
				UUID associatedplayer = NerdUHC.combatLogger.getDoppelPlayer(doppel);
				NerdUHC.combatLogger.getDoppelDrops(associatedplayer).forEach(item -> e.getDrops().add(item));
				
			}
			
		}
		
	}
	
}
