package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.bermudalocket.nerdUHC.NerdUHC;

/////////////////////////////////////////////////////////////////////////////
//
//	Listener: PlayerDeathEvent
//  Executes: calls method which resets score, removes from team, etc
//

public class ListenForPlayerDeathEvent implements Listener {
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Listen
	//
	//
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		if (NerdUHC.isGameStarted()) {
			
			if (NerdUHC.scoreboardHandler.getPlayerScore(player, NerdUHC.CONFIG.DEATH_OBJECTIVE_NAME) == 0) {
				
				NerdUHC.scoreboardHandler.setPlayerScore(player, NerdUHC.CONFIG.DEATH_OBJECTIVE_NAME, 1);
				NerdUHC.scoreboardHandler.removePlayerTeam(player);
				NerdUHC.scoreboardHandler.setPlayerTeam(player, NerdUHC.CONFIG.DEAD_TEAM_NAME);
				
			}
		} 
	}
}
