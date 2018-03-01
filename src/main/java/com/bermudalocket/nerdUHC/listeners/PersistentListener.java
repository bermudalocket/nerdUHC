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
import com.bermudalocket.nerdUHC.modules.UHCLibrary;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;

public class PersistentListener implements Listener {

	private final NerdUHC plugin;
	private final FixSpectatorRunnable fixSpectatorRunnable;

	public PersistentListener(NerdUHC plugin) {
		this.plugin = plugin;
		this.fixSpectatorRunnable = new FixSpectatorRunnable(plugin);
	}
	
	@EventHandler
	public void onGameModeChange(PlayerGameModeChangeEvent e) {
		if (e.getPlayer() != null && e.getPlayer().isOnline()) {
			fixSpectatorRunnable.setState(e.getPlayer(), e.getNewGameMode() == GameMode.SPECTATOR);
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		UHCMatch match = plugin.matchHandler.getMatch();
		Player p = e.getPlayer();
		ChatColor color;
		Team t = match.getScoreboard().getEntryTeam(p.getName());
		
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

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		UHCMatch match = plugin.matchHandler.getMatch();
		Player p = e.getPlayer();

		// set display name and player list colors
		Team t = match.getScoreboard().getEntryTeam(p.getName());
		if (t == null) {
			handleNewPlayer(p, match);
		} else {
			p.setPlayerListName(t.getColor() + p.getName());
			p.setDisplayName(t.getColor() + p.getName() + ChatColor.WHITE);
		}

		// run some tasks that need to be run 1 tick after joining
		BukkitRunnable playerJoinTask = new BukkitRunnable() {
			@Override
			public void run() {
				p.setScoreboard(match.getScoreboard());
				match.getScoreboardHandler().refresh();
				match.getCombatLogger().reconcileDoppelWithPlayer(p);
			}
		};
		playerJoinTask.runTaskLater(plugin, 1);
	}
	
	private void handleNewPlayer(Player p, UHCMatch match) {
		p.teleport(match.getWorld().getSpawnLocation());
		UHCLibrary.LIB_WELCOME.rep(p, "%t", match.getGameMode().toString());
		if (match.getMatchState() == UHCMatchState.PREGAME) {
			match.resetPlayer(p, false);
			match.getGUI().givePlayerGUIItems(p);
			UHCLibrary.LIB.welcome(p, match.getGameMode());
		} else {
			p.setGameMode(GameMode.SPECTATOR);
			p.setDisplayName(ChatColor.GRAY + "" + ChatColor.ITALIC + p.getName() + ChatColor.WHITE);
			UHCLibrary.LIB_IN_PROGRESS.tell(p);
		}
	}

}
