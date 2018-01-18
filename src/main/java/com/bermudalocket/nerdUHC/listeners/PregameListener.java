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
import com.bermudalocket.nerdUHC.modules.UHCMatchState;

public class PregameListener implements Listener {
	
	private NerdUHC plugin;
	
	private final String LIB_WELCOME = ChatColor.GOLD + "Welcome to nerdUHC. The next round will be a %t round.";
	private final String LIB_SOLO_JOIN = ChatColor.GOLD + "To join, run " + ChatColor.WHITE + "/join";
	private final String LIB_SPEC = ChatColor.GOLD + "To spectate, run " + ChatColor.WHITE + "/join spectator";
	private final String LIB_TEAM_LIST = ChatColor.GOLD + "To view a list of teams, run " + ChatColor.WHITE + "/teamlist";
	private final String LIB_TEAM_JOIN = ChatColor.GOLD + "To join a team, run " + ChatColor.WHITE + "/join [team]";
	
	public PregameListener(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
			
		UUID player = e.getPlayer().getUniqueId();
		Player p = Bukkit.getPlayer(player);
		UHCGameMode mode = plugin.match.getGameMode();
		
		if (!plugin.match.getMatchState().equals(UHCMatchState.PREGAME)) return;
		
		p.teleport(plugin.CONFIG.SPAWN);
		
		p.sendMessage(LIB_WELCOME.replace("%t", mode.toString()));
		
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
