package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.bermudalocket.nerdUHC.nerdUHC;

public class ListenForPlayerDeathEvent implements Listener {
	
	public nerdUHC plugin;
	
	public Player player;
	
	public ListenForPlayerDeathEvent(nerdUHC plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		
		player = e.getEntity();
		
		if (plugin.gameStarted) {
			
			if (plugin.ScoreboardHandler.GetPlayerScore(player, "Deaths") == 0) {
				
				plugin.ScoreboardHandler.SetPlayerScore(player, "Deaths", 1);
				plugin.ScoreboardHandler.RemovePlayerTeam(player);
				plugin.ScoreboardHandler.SetPlayerTeam(player, "Dead");
				
			}
			
		}
		
	} // onPlayerDeath
} // class
