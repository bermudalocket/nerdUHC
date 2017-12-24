package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.bermudalocket.nerdUHC.nerdUHC;

public class ListenForPlayerJoinEvent implements Listener {
	
	public nerdUHC plugin;
	public Player player;
	
	public ListenForPlayerJoinEvent(nerdUHC plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent e) {

		try { 
			plugin.ScoreboardHandler.SetPlayerBoard(e.getPlayer());
		} catch (IllegalArgumentException f) {
			// board doesnt exist, but it should. wtf happened.
			return;
		} catch (IllegalStateException g) {
			// player dropped/quit/didnt fully login
			return;
		}
		
	}
	
}
