package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
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
	public void onGameModeChange(PlayerGameModeChangeEvent e) {
		Player p = e.getPlayer();
		
		if (e.getNewGameMode() == GameMode.SPECTATOR || e.getNewGameMode() == GameMode.CREATIVE) {
			p.setAllowFlight(true);
			p.setFlying(true);
		} else {
			p.setAllowFlight(false);
			p.setFlying(false);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {

		UHCMatch match = plugin.matchHandler.getMatch();
		Player p = e.getPlayer();

		if (!match.getScoreboardHandler().isPlayerOnBoard(p)) {
			
			p.teleport(match.getSpawn());
			UHCLibrary.LIB_WELCOME.rep(p, "%t", match.getGameMode().toString());

			if (match.getMatchState() == UHCMatchState.PREGAME) {
				
				match.resetPlayer(p);
				
				match.getGUI().givePlayerGUIItems(p);
				if (p.hasPermission("nerduhc.gamemaster")) {
					match.getGUI().giveGamemasterGUIItems(p);
				}
				
				if (match.getGameMode() == UHCGameMode.SOLO) {
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
				p.setDisplayName(ChatColor.GRAY + "" + ChatColor.ITALIC + p.getName() + ChatColor.WHITE);
				UHCLibrary.LIB_IN_PROGRESS.get(p);
				
			}
		} else {
			Team t = match.getScoreboard().getPlayerTeam(p);
			p.setPlayerListName(t.getColor() + p.getName());
			p.setDisplayName(t.getColor() + p.getName() + ChatColor.WHITE);
			
			if (match.getMatchState() != UHCMatchState.PREGAME) {
				BukkitRunnable attemptReconcileDoppelTask = new BukkitRunnable() {
					@Override
					public void run() {
						match.getCombatLogger().reconcileDoppelWithPlayer(p);
					}
				};
				attemptReconcileDoppelTask.runTaskLater(plugin, 1);
			}
		}
		
		BukkitRunnable setScoreboardTask = new BukkitRunnable() {
			@Override
			public void run() {
				p.setScoreboard(match.getScoreboard());
				match.getScoreboardHandler().refresh();
			}
		};
		setScoreboardTask.runTaskLater(plugin, 1);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		
		UHCMatch match = plugin.matchHandler.getMatch();
		Player p = e.getPlayer();
		ChatColor color;
		Team t = match.getScoreboard().getPlayerTeam(p);
		
		if (t == null) {
			color = ChatColor.WHITE;
		} else {
			color = t.getColor();
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage("<" + color + p.getName() + ChatColor.WHITE + "> " + e.getMessage());
		}

		e.setCancelled(true);
	}

}
