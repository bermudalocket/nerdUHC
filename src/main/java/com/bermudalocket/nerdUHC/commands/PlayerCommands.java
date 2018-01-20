package com.bermudalocket.nerdUHC.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.events.PlayerChangeTeamEvent;
import com.bermudalocket.nerdUHC.modules.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;
import com.bermudalocket.nerdUHC.modules.UHCSound;
import com.bermudalocket.nerdUHC.modules.UHCTeam;

public class PlayerCommands implements CommandExecutor {

	private NerdUHC plugin;
	
	public PlayerCommands(NerdUHC plugin) {
		this.plugin = plugin;
		plugin.getCommand("join").setExecutor(this);
		plugin.getCommand("teamlist").setExecutor(this);
		plugin.getCommand("t").setExecutor(this);
		plugin.getCommand("fixme").setExecutor(this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		UHCPlayer p = plugin.match.getPlayer(((Player) sender).getUniqueId());
		
		if (cmd.getName().equalsIgnoreCase("fixme")) {
			if (p.bukkitPlayer().getGameMode() == GameMode.SPECTATOR) {
				p.bukkitPlayer().teleport(plugin.CONFIG.SPAWNFIXME);
				p.bukkitPlayer().setFlying(true);
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("t")) {
			if (p.hasTeam()) {
				UHCTeam t = p.getTeam();
				StringBuilder msg = new StringBuilder();
				msg.append("[");
				msg.append(t.getDisplayName());
				msg.append("] <");
				msg.append(p.getName());
				msg.append("> ");
				for (int i = 0; i < args.length; i++) {
					msg.append(args[i] + " ");
				}
				for (UHCPlayer teammate : t.getPlayers()) {
					teammate.bukkitPlayer().sendMessage(msg.toString());
				}
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("join")) {
			if (plugin.match.getGameMode().equals(UHCGameMode.SOLO)) {
				if (p.getTeam() == null) {
					PlayerChangeTeamEvent e = new PlayerChangeTeamEvent(p, plugin.match.getTeam("ALIVE"));
					Bukkit.getServer().getPluginManager().callEvent(e);
				} else {
					sender.sendMessage(ChatColor.RED + "You're already registered!");
					UHCSound.OOPS.playSound(p);
				}
			} else if (plugin.match.getGameMode().equals(UHCGameMode.TEAM)) {
				if (args.length == 1) {
					String team = args[0].toString();
					if (teamIsJoinable(team)) {
						plugin.call(new PlayerChangeTeamEvent(p, plugin.match.getTeam(team)));
					} else {
						sender.sendMessage(ChatColor.RED + "That team is either full or doesn't exist!");
						UHCSound.OOPS.playSound(p);
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Invalid syntax: /join [team].");
					UHCSound.OOPS.playSound(p);
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
		if (plugin.match.getMatchState().equals(UHCMatchState.PREGAME)) {
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
