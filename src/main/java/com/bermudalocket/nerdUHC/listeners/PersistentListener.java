package com.bermudalocket.nerdUHC.listeners;

import com.bermudalocket.nerdUHC.thread.PlayerJoinThread;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.match.Match;

import static com.bermudalocket.nerdUHC.NerdUHC.MATCH_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.SCOREBOARD_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.THREAD_HANDLER;

public class PersistentListener implements Listener {

	public PersistentListener() {
		NerdUHC.PLUGIN.getServer().getPluginManager().registerEvents(this, NerdUHC.PLUGIN);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		Team t = SCOREBOARD_HANDLER.getTeamByPlayer(p);
		ChatColor color = (t == null) ? ChatColor.WHITE : t.getColor();
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage("<" + color + p.getName() + ChatColor.WHITE + "> " + e.getMessage());
		}

		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Match match = MATCH_HANDLER.getMatch();
		Player player = e.getPlayer();

		THREAD_HANDLER.schedule(1L, new PlayerJoinThread(match, player));
	}

}
