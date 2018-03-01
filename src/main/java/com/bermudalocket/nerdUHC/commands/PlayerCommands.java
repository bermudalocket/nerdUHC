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

public class PlayerCommands implements CommandExecutor {

	private final NerdUHC plugin;
	
	public PlayerCommands(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) return false;
		Player p = (Player) sender;
		
		UHCMatch match = plugin.matchHandler.getMatch();
		
		if (cmd.getName().equalsIgnoreCase("kit")) {
			if (match.getMatchState() == UHCMatchState.PREGAME) {
				p.getInventory().clear();
				match.getGUI().givePlayerGUIItems(p);
			} else {
				UHCLibrary.LIB_ERR_NOKIT.err(p);
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("sb")) {
			p.setScoreboard(match.getScoreboard());
			UHCLibrary.LIB_SCOREBOARD_REFRESHED.tell(p);
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("fixme")) {
			if (p.getGameMode() == GameMode.SPECTATOR) {
				p.setAllowFlight(true);
				p.setFlying(true);
				p.teleport(match.getWorld().getSpawnLocation());
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
			for (String arg : args) {
				msg.append(arg).append(" ");
			}
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if (t.getEntries().contains(onlinePlayer.getName())) {
					onlinePlayer.sendMessage(msg.toString());
				}
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("join")) {
			if (args.length == 1) {
				String team = args[0].toUpperCase();
				if (teamIsJoinable(match, team)) {
					if (p.getGameMode() == GameMode.SPECTATOR) {
						p.setGameMode(GameMode.SURVIVAL);
					}
					match.getScoreboardHandler().addPlayerToTeam(p, team);
				} else {
					UHCLibrary.LIB_ERR_TEAM_FULL.err(p);
				}
			} else {
				UHCLibrary.LIB_ERR_JOIN_SYNTAX.err(p);
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("teamlist")) {
			if (match.getGameMode() == UHCGameMode.TEAM) {
				match.getScoreboard().getTeams().forEach(t -> sender.sendMessage(t.getDisplayName() + ChatColor.WHITE + "(" + t.getSize() + "/" + plugin.CONFIG.MAX_TEAM_SIZE + ")"));
			}
			return true;
		}
		
		return false;
	}
	
	private boolean teamIsJoinable(UHCMatch match, String team) {
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
