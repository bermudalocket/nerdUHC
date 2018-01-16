package com.bermudalocket.nerdUHC.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.events.PlayerChangeTeamEvent;
import com.bermudalocket.nerdUHC.modules.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;

public class PlayerCommands implements CommandExecutor {

	private NerdUHC plugin;
	
	public PlayerCommands(NerdUHC plugin) {
		this.plugin = plugin;
		plugin.getCommand("join").setExecutor(this);
		plugin.getCommand("teamlist").setExecutor(this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		UHCPlayer p = plugin.match.getPlayer(((Player) sender).getUniqueId());
		
		if (cmd.getName().equalsIgnoreCase("join")) {
			if (plugin.match.getGameMode().equals(UHCGameMode.SOLO)) {
				if (p.getTeam() != null) {
					PlayerChangeTeamEvent e = new PlayerChangeTeamEvent(p, plugin.match.getTeam("ALIVE"));
					Bukkit.getServer().getPluginManager().callEvent(e);
				} else {
					sender.sendMessage(ChatColor.RED + "You're already registered!");
				}
			} else if (plugin.match.getGameMode().equals(UHCGameMode.TEAM)) {
				if (args.length == 1) {
					String team = args[0].toString();
					if (teamIsJoinable(team)) {
						PlayerChangeTeamEvent e = new PlayerChangeTeamEvent(p, plugin.match.getTeam(team));
						Bukkit.getServer().getPluginManager().callEvent(e);
					} else {
						sender.sendMessage(ChatColor.RED + "That team is either full or doesn't exist!");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Invalid syntax: /join [team].");
				}
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("teamlist")) {
			if (plugin.match.allowTeams()) {
				plugin.match.getTeams().forEach(t -> {
					sender.sendMessage(t.getColor() + t.getName() + ChatColor.WHITE + "(" + t.getSize() + "/" + t.getMaxSize() + ")");
				});
			}
			return true;
		}
		
		return false;
	}
	
	public boolean teamIsJoinable(String team) {
		if (!plugin.match.isGameStarted()) {
			if (plugin.match.teamExists(team)) {
				if (!plugin.match.getTeam(team).isFull()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean teamIsLeaveable(UHCPlayer p) {
		if (p.hasTeam() && p.isAlive()) { 
			return true;
		}
		return false;
	}
	
}
