package com.bermudalocket.nerdUHC.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCLibrary;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;

public class PersistentListener implements Listener {

	private NerdUHC plugin;
	
	public PersistentListener(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
		UUID player = e.getPlayer().getUniqueId();
		Player p = Bukkit.getPlayer(player);
		UHCMatch match = plugin.matchHandler.getMatchByPlayer(p);
		
		if (match == null) {
			UHCMatch currentmatch = plugin.matchHandler.getMatch();
			UHCGameMode mode = currentmatch.getGameMode();
			
			p.teleport(currentmatch.getSpawn());
			p.setGameMode(GameMode.SURVIVAL);
			p.setScoreboard(currentmatch.getScoreboard());
			plugin.scoreboardHandler.showTeamCountCapacity(currentmatch);

			UHCLibrary.LIB_WELCOME.rep(p, "%t", mode.toString());
			
			if (currentmatch.getMatchState() == UHCMatchState.PREGAME) {
				currentmatch.addPlayer(p);
				
				if (mode == UHCGameMode.SOLO) {
					UHCLibrary.LIB_SOLO_JOIN.get(p);
					UHCLibrary.LIB_SPEC.get(p);
				} else {
					UHCLibrary.LIB_TEAM_LIST.get(p);
					UHCLibrary.LIB_TEAM_JOIN.get(p);
					UHCLibrary.LIB_TEAM_CHAT.get(p);
					UHCLibrary.LIB_SPEC.get(p);
				}
			} else {
				p.setGameMode(GameMode.SPECTATOR);
				UHCLibrary.LIB_IN_PROGRESS.get(p);
			}
		} else {
			p.setScoreboard(match.getScoreboard());
			plugin.scoreboardHandler.showTeamCountCapacity(match);
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		UHCMatch match = plugin.matchHandler.getMatchByPlayer(p);
		
		ChatColor color;
		if (match == null) {
			plugin.getLogger().info("match null");
			color = ChatColor.WHITE;
		} else {
			Team t = match.getTeamForPlayer(p);
			if (t == null) {
				plugin.getLogger().info("team null");
				color = ChatColor.WHITE;
			} else {
				color = t.getColor();
			}
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage("<" + color + p.getName() + ChatColor.WHITE + "> " + e.getMessage());
		}
		
		e.setCancelled(true);
	}
	
}
