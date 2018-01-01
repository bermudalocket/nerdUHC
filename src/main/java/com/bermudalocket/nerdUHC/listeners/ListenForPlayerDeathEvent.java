package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.ChatColor;

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
				if (player.getLastDamageCause().getCause().equals(DamageCause.VOID)) {
					e.setDeathMessage(player.getDisplayName() + ChatColor.RED + " thought they could get away with combat logging");
				} else if (player.getKiller() != null) {
					e.setDeathMessage(ChatColor.RED + "Down falls " + player.getDisplayName() + ChatColor.RED + ", killed by " + player.getKiller());
				} else if (player.getKiller() == null) {
					e.setDeathMessage(ChatColor.RED + "Down falls " + player.getDisplayName() + ChatColor.RED + ", killed by the Olmecs");
				}
				NerdUHC.scoreboardHandler.setPlayerScore(player, NerdUHC.CONFIG.DEATH_OBJECTIVE_NAME, 1);
				NerdUHC.scoreboardHandler.removePlayerTeam(player);
				NerdUHC.scoreboardHandler.setPlayerTeam(player, NerdUHC.CONFIG.DEAD_TEAM_NAME);
				player.setDisplayName(ChatColor.STRIKETHROUGH + player.getName());
				player.setPlayerListName(ChatColor.STRIKETHROUGH + player.getName());
			}
		} 
	}
}
