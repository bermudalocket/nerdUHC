package com.bermudalocket.nerdUHC.commands;

import com.bermudalocket.nerdUHC.Configuration;
import com.bermudalocket.nerdUHC.util.MatchMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.match.Match;
import com.bermudalocket.nerdUHC.util.MatchState;
import com.bermudalocket.nerdUHC.util.UHCLibrary;
import org.bukkit.scoreboard.Team;

import static com.bermudalocket.nerdUHC.NerdUHC.GUI_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.MATCH_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.SCOREBOARD_HANDLER;

public class Commands implements CommandExecutor {

	public Commands() {
		// player commands
		NerdUHC.PLUGIN.getCommand("join").setExecutor(this);
		NerdUHC.PLUGIN.getCommand("t").setExecutor(this);
		NerdUHC.PLUGIN.getCommand("teamlist").setExecutor(this);
		NerdUHC.PLUGIN.getCommand("fixme").setExecutor(this);
		NerdUHC.PLUGIN.getCommand("kit").setExecutor(this);
		NerdUHC.PLUGIN.getCommand("rules").setExecutor(this);

		// gamemaster commands
		NerdUHC.PLUGIN.getCommand("uhc").setExecutor(this);
		NerdUHC.PLUGIN.getCommand("sb-all").setExecutor(this);
	}

	// -------------------------------------------------------------------------------
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		Match match = MATCH_HANDLER.getMatch();

		// -------------------------------------------------------------------------------
		// player commands
		// -------------------------------------------------------------------------------

		if (cmd.getName().equalsIgnoreCase("rules")) {
			for (String string : Configuration.RULES) {
				player.sendMessage(string);
			}
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("kit")) {
			if (match.inState(MatchState.PREGAME)) {
				player.getInventory().clear();
				GUI_HANDLER.givePlayerGUIItems(player);
			} else {
				UHCLibrary.LIB_ERR_NOKIT.err(player);
			}
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("fixme")) {
			if (player.getGameMode() == GameMode.SPECTATOR) {
				player.setAllowFlight(true);
				player.setFlying(true);
				player.teleport(match.getWorld().getSpawnLocation());
			}
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("t")) {
			Team t = SCOREBOARD_HANDLER.getTeamByPlayer(player);
			if (t == null) {
				UHCLibrary.LIB_ERR_NOTEAMFORCHAT.err(player);
				return true;
			}
			StringBuilder msg = new StringBuilder();
			msg.append("[");
			msg.append(t.getDisplayName());
			msg.append("] <");
			msg.append(player.getName());
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
				if (SCOREBOARD_HANDLER.teamIsJoinable(team)) {
					SCOREBOARD_HANDLER.addPlayerToTeam(player, team);
				} else {
					UHCLibrary.LIB_ERR_TEAM_FULL.err(player);
				}
			} else {
				UHCLibrary.LIB_ERR_JOIN_SYNTAX.err(player);
			}
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("teamlist")) {
			if (match.getGameMode() == MatchMode.TEAM) {
				SCOREBOARD_HANDLER.getRegisteredTeams().forEach(t ->
						sender.sendMessage(t.getDisplayName() + ChatColor.WHITE + "(" + t.getSize() + "/" + Configuration.MAX_TEAM_SIZE + ")"));
			}
			return true;
		}

		// -------------------------------------------------------------------------------
		// gamemaster commands
		// -------------------------------------------------------------------------------
		
		if (cmd.getName().equalsIgnoreCase("sb-all")) {
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				SCOREBOARD_HANDLER.setPlayerBoard(onlinePlayer);
				UHCLibrary.LIB_SCOREBOARD_ALL_REFRESHED.tell(player);
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("start")) {
					if (match.getMatchState().equals(MatchState.PREGAME)) {
						match.startCountdown();
					} else {
						UHCLibrary.LIB_ERR_UHC_RUNNING.err(player);
					}
					return true;
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("stop")) {
					if (match.getMatchState() != MatchState.PREGAME) {
						match.transitionToEnd("Match ending early...");
					} else {
						UHCLibrary.LIB_ERR_NO_UHC_RUNNING.err(player);
					}
					return true;
				}
			}
		}

		return false;
	}
	
}
