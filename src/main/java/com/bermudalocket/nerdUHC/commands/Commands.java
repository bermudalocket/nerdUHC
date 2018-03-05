package com.bermudalocket.nerdUHC.commands;

import com.bermudalocket.nerdUHC.modules.UHCGameMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCLibrary;
import org.bukkit.scoreboard.Team;

public class Commands implements CommandExecutor {

	private final NerdUHC plugin;

	// -------------------------------------------------------------------------------
	
	public Commands() {
		this.plugin = NerdUHC.plugin;

		// player commands
		plugin.getCommand("join").setExecutor(this);
		plugin.getCommand("t").setExecutor(this);
		plugin.getCommand("teamlist").setExecutor(this);
		plugin.getCommand("fixme").setExecutor(this);
		plugin.getCommand("sb").setExecutor(this);
		plugin.getCommand("kit").setExecutor(this);

		// gamemaster commands
		plugin.getCommand("uhc").setExecutor(this);
		plugin.getCommand("sb-all").setExecutor(this);
		plugin.getCommand("togglepvp").setExecutor(this);
		plugin.getCommand("extendtime").setExecutor(this);
	}

	// -------------------------------------------------------------------------------
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) return false;
		Player p = (Player) sender;
		UHCMatch match = plugin.matchHandler.getMatch();

		// -------------------------------------------------------------------------------
		// player commands
		// -------------------------------------------------------------------------------

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
				if (match.getScoreboardHandler().teamIsJoinable(match, team)) {
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
				match.getScoreboard().getTeams().forEach(t -> sender.sendMessage(t.getDisplayName() + ChatColor.WHITE + "(" + t.getSize() + "/" + plugin.config.MAX_TEAM_SIZE + ")"));
			}
			return true;
		}

		// -------------------------------------------------------------------------------
		// gamemaster commands
		// -------------------------------------------------------------------------------

		if (cmd.getName().equalsIgnoreCase("shrinkwb")) {
			if (match.getWorldBorder().isShrinking()) {
				match.getWorldBorder().shrink();
			} else {
				match.getWorldBorder().freeze();
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("extendtime")) {
			if (args.length != 1) return false;
			
			try {
				int sec = Integer.valueOf(args[0]);
				match.getMatchTimer().extend(sec);
			} catch (Exception f) {
				return false;
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("togglepvp")) {
			if (match.getWorld().getPVP()) {
				match.getWorld().setPVP(false);
				UHCLibrary.LIB_PVP_DISABLED.tell(p);
			} else {
				match.getWorld().setPVP(true);
				UHCLibrary.LIB_PVP_ENABLED.tell(p);
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("sb-all")) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.setScoreboard(match.getScoreboard());
				UHCLibrary.LIB_SCOREBOARD_ALL_REFRESHED.tell(p);
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("start")) {
					if (match.getMatchState().equals(UHCMatchState.PREGAME)) {
						match.beginMatchStartCountdown();
					} else {
						UHCLibrary.LIB_ERR_UHC_RUNNING.err(p);
					}
					return true;
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("stop")) {
					if (match.getMatchState() != UHCMatchState.PREGAME) {
						match.endMatch();
					} else {
						UHCLibrary.LIB_ERR_NO_UHC_RUNNING.err(p);
					}
					return true;
				}
			}
		}

		return false;
		
	}
	
}
