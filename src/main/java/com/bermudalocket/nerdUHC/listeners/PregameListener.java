package com.bermudalocket.nerdUHC.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCGameMode;

public class PregameListener implements Listener {
	
	private NerdUHC plugin;
	
	public PregameListener(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
			
		UUID player = e.getPlayer().getUniqueId();
		Player p = Bukkit.getPlayer(player);
		UHCGameMode mode = plugin.match.getGameMode();
		
		String LIB_WELCOME = ChatColor.GRAY + "Welcome to nerdUHC. The next round will be a " + mode.toString() + " round.";
		String LIB_SOLO_JOIN = ChatColor.GRAY + "To join, run " + ChatColor.WHITE + "/join";
		String LIB_SPEC = ChatColor.GRAY + "To spectate, run " + ChatColor.WHITE + "/join spectator";
		String LIB_TEAM_LIST = ChatColor.GRAY + "To view a list of teams, run " + ChatColor.WHITE + "/teamlist";
		String LIB_TEAM_JOIN = ChatColor.GRAY + "To join a team, run " + ChatColor.WHITE + "/join [team]";
		
		if (plugin.match.isGameStarted()) return;
		
		p.teleport(plugin.match.getSpawn());
		
		p.sendMessage(LIB_WELCOME);
		
		if (!plugin.match.playerExists(player)) {
			if (mode.equals(UHCGameMode.SOLO)) {
				p.sendMessage(LIB_SOLO_JOIN);
				p.sendMessage(LIB_SPEC);
			} else {
				p.sendMessage(LIB_TEAM_LIST);
				p.sendMessage(LIB_TEAM_JOIN);
				p.sendMessage(LIB_SPEC);
			}
		}
		
	}
	
	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent e) {
		if (!plugin.match.isGameStarted()) e.setCancelled(true);
	}
}
