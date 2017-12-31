package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.bermudalocket.nerdUHC.NerdUHC;

/////////////////////////////////////////////////////////////////////////////
//
//	Listener: EntityDamageByEntityEvent
//  Executes: Combat tagging
//

public class ListenForChunkUnloadEvent implements Listener {
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Listen
	//
	//
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onChunkUnload(ChunkUnloadEvent e) {
		if (NerdUHC.combatLogger.doesChunkHaveDoppel(e.getChunk())) {
			e.setCancelled(true);
		}

	}
	
}
