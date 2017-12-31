package com.bermudalocket.nerdUHC.listeners;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.bermudalocket.nerdUHC.NerdUHC;

/////////////////////////////////////////////////////////////////////////////
//
//	Listener: PlayerQuitEvent
//  Executes: CombatLogger checks & prevention
//

public class ListenForPlayerQuitEvent implements Listener {
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Listen
	//
	//
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerQuit(PlayerQuitEvent e) {
		
		UUID player = e.getPlayer().getUniqueId();
		Location playerlocation = e.getPlayer().getLocation();
		
		if (NerdUHC.combatLogger.isPlayerTagged(player)) {
			
			NerdUHC.combatLogger.spawnDoppel(player, playerlocation);
			
		}
	}
}
