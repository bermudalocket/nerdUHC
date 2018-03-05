package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCLibrary;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;

public class PersistentListener implements Listener {

	private final NerdUHC plugin;

	public PersistentListener() {
		plugin = NerdUHC.plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
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

		handleNewPlayer(p, match);

		// run some tasks that need to be run 1 tick after joining
		BukkitRunnable playerJoinTask = new BukkitRunnable() {
			@Override
			public void run() {
				UHCLibrary.LIB_WELCOME.rep(p, "%t", match.getGameMode().toString());
				p.setScoreboard(match.getScoreboard());
				match.getScoreboardHandler().refresh();
				plugin.combatLogger.reconcileDoppelWithPlayer(p);
			}
		};
		playerJoinTask.runTaskLater(plugin, 1);
	}
	
	private void handleNewPlayer(Player p, UHCMatch match) {
		match.getScoreboardHandler().formatDisplayName(p);

		if (!match.getScoreboardHandler().hasTeam(p)) {
			p.teleport(match.getWorld().getSpawnLocation());
			if (match.getMatchState() == UHCMatchState.PREGAME) {
				match.resetPlayer(p);
				match.getGUI().givePlayerGUIItems(p);
			} else {
				match.getScoreboardHandler().makeSpectator(p);
			}
		}
	}

}
