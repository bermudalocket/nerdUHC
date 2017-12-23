package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.bermudalocket.nerdUHC.nerdUHC;

public class ListenForPlayerLoginEvent implements Listener {
	
	public nerdUHC plugin;
	public Player player;
	
	public ListenForPlayerLoginEvent(nerdUHC plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerLogin(PlayerLoginEvent e) {
		try { 
			plugin.ScoreboardHandler.SetPlayerBoard(e.getPlayer());
			// TESTING ONLY:
			plugin.ScoreboardHandler.SetPlayerTeam(e.getPlayer(), "Alive");
			e.getPlayer().sendMessage("added to board");
		} catch (IllegalArgumentException f) {
			// board doesnt exist, but it should. wtf happened.
			plugin.reportError("Scoreboard missing during player login event. It shouldn't be missing.");
		} catch (IllegalStateException g) {
			// player dropped/quit/didnt fully login
			return;
		}
	}
	
}
