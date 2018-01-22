package com.bermudalocket.nerdUHC.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCLibrary;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCSound;

public class PlayerCommands implements CommandExecutor {

	private NerdUHC plugin;
	
	public PlayerCommands(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) return false;
		
		UHCMatch match = plugin.matchHandler.getMatchByPlayer((Player) sender);
		if (match == null) return true;
		
		Player p = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("fixme")) {
			if (p.getGameMode() == GameMode.SPECTATOR) {
				p.teleport(plugin.CONFIG.SPAWNFIXME);
				p.setFlying(true);
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("t")) {
			if (match.isPlayerInMatch(p.getUniqueId())) {
				Team t = match.getScoreboard().getEntryTeam(p.getName());
				if (t == null) {
					UHCLibrary.LIB_ERR_NOTEAMFORCHAT.err(p);
					return true;
				}
				StringBuilder msg = new StringBuilder();
				msg.append("[");
				msg.append(t.getDisplayName());
				msg.append("] <");
				msg.append(p.getName());
				msg.append("> ");
				for (int i = 0; i < args.length; i++) {
					msg.append(args[i] + " ");
				}
				for (String entry : t.getEntries()) {
					Bukkit.getPlayer(entry).sendMessage(msg.toString());
				}
			} else {
				plugin.getLogger().info("nope");
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("join")) {
			if (match.getGameMode() == UHCGameMode.SOLO) {
				//
			} else if (match.getGameMode() == UHCGameMode.TEAM) {
				if (args.length == 1) {
					String team = args[0].toString().toUpperCase();
					if (teamIsJoinable(match, team)) {
						Team t = match.getScoreboard().getTeam(team);
						t.addEntry(p.getName());
						sender.sendMessage("You joined the " + t.getDisplayName() + " team!");
						p.setDisplayName(t.getColor() + p.getName());
						plugin.scoreboardHandler.showTeamCountCapacity(match);
						plugin.scoreboardHandler.forceHealthUpdates();
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
			if (match.getGameMode() == UHCGameMode.TEAM) {
				match.getScoreboard().getTeams().forEach(t -> {
					sender.sendMessage(t.getDisplayName() + ChatColor.WHITE + "(" + t.getSize() + "/" + plugin.CONFIG.MAX_TEAM_SIZE + ")");
				});
			}
			return true;
		}
		
		return false;
	}
	
	public boolean teamIsJoinable(UHCMatch match, String team) {
		if (match.getMatchState() == UHCMatchState.PREGAME) {
			try {
				Team t = match.getScoreboard().getTeam(team);
				if (t.getSize() < plugin.CONFIG.MAX_TEAM_SIZE) {
					return true;
				}
			} catch (Exception f) {
				return false;
			}
		}
		return false;
	}
	
}
