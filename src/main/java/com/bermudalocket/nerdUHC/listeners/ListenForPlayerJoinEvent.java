package com.bermudalocket.nerdUHC.listeners;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;

/////////////////////////////////////////////////////////////////////////////
//
//	Listener: PlayerJoinEvent
//  Executes: sets up initial player config, handles CombatLogger doppel reconciliation
//

public class ListenForPlayerJoinEvent implements Listener {
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Listen
	//
	//
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent e) {
		
		UUID player = e.getPlayer().getUniqueId();
		
		if (NerdUHC.combatLogger.doesPlayerHaveDoppel(player)) {
			
			BukkitRunnable task = new BukkitRunnable() {
				@Override
				public void run() {
					NerdUHC.combatLogger.reconcileDoppelWithPlayer(player);
				}
			};
			task.runTaskLater(NerdUHC.PLUGIN, 1);
			
		}
		NerdUHC.registerPlayer(e.getPlayer(), false);		// true = ignore "has UHC started" check
		
	}
	
}
