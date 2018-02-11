package com.bermudalocket.nerdUHC.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
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
		Player p = (Player) sender;
		
		UHCMatch match = plugin.matchHandler.getMatch();
		
		if (cmd.getName().equalsIgnoreCase("kit")) {
			if (match.getMatchState() == UHCMatchState.PREGAME) {
				p.getInventory().clear();
				if (p.hasPermission("nerduhc.gamemaster")) {
					match.getGUI().giveGamemasterGUIItems(p);
				} else {
					match.getGUI().givePlayerGUIItems(p);
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("sb")) {
			p.setScoreboard(match.getScoreboard());
			UHCLibrary.LIB_SCOREBOARD_REFRESHED.get(p);
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("fixme")) {
			if (p.getGameMode() == GameMode.SPECTATOR) {
				p.setAllowFlight(true);
				p.setFlying(true);
				p.teleport(match.getSpawn());
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("t")) {
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
			for (OfflinePlayer op : t.getPlayers()) {
				if (op.isOnline()) {
					Player player = Bukkit.getPlayer(op.getUniqueId());
					player.sendMessage(msg.toString());
				}
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
						if (p.getGameMode() == GameMode.SPECTATOR) {
							p.setGameMode(GameMode.SURVIVAL);
						}
						Team t = match.getScoreboard().getTeam(team);
						t.addPlayer(p);
						sender.sendMessage("You joined the " + t.getDisplayName() + " team!");
						UHCSound.JOINTEAM.playSound(p);
						p.setDisplayName(t.getColor() + p.getName());
						p.setPlayerListName(t.getColor() + p.getName());
						match.getScoreboardHandler().refresh();
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
