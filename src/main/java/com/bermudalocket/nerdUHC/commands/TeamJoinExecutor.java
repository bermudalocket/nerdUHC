package com.bermudalocket.nerdUHC.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.Match.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;

public class TeamJoinExecutor extends CommandHandler {
	
	private NerdUHC plugin;
	
	public TeamJoinExecutor(NerdUHC plugin) {
		super("join", "help");
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		
		Player p = (Player) sender;
		UUID player = p.getUniqueId();
		
		UHCGameMode mode = plugin.match.getGameMode();
		
		String LIB_GAME_STARTED = ChatColor.RED + "Sorry! You can't alter your team after a round has begun.";
		String LIB_JOINED_ALIVE = ChatColor.GRAY + "You have joined the " + plugin.CONFIG.ALIVE_TEAM_NAME + " team.";
		String LIB_NO_SUCH_TEAM = ChatColor.RED + "That team doesn't exist!";
		String LIB_TEAM_FULL = ChatColor.RED + "Sorry, that team is full!";
		String LIB_TEAM_JOIN = ChatColor.GRAY + "You have joined the %t team.";
		
		if (plugin.match.isGameStarted()) {
			p.sendMessage(LIB_GAME_STARTED);
			return true;
		}
		
		if (plugin.match.playerExists(player)) {
			UHCPlayer uhcplayer = plugin.match.getPlayer(player);
			
			if (mode.equals(UHCGameMode.SOLO)) {
				// youre already registered
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("spectator")) {
				uhcplayer.setTeam("SPECTATOR");
				return true;
			} else if (args.length == 1 && mode.equals(UHCGameMode.TEAM)) {
				String team = args[0].toString().toUpperCase();
				if (!plugin.scoreboardHandler.teamExists(team)) {
					p.sendMessage(LIB_NO_SUCH_TEAM);
					return true;
				}
				if (plugin.scoreboardHandler.isTeamFull(team)) {
					p.sendMessage(LIB_TEAM_FULL);
					return true;
				}
				uhcplayer.setTeam(team);
				plugin.scoreboardHandler.forceHealthUpdates();
				p.sendMessage(LIB_TEAM_JOIN.replace("%t", team));
				return true;
			}
		} else {
			if (mode.equals(UHCGameMode.SOLO)) {
				plugin.match.registerPlayer(player, plugin.CONFIG.ALIVE_TEAM_NAME);
				p.sendMessage(LIB_JOINED_ALIVE);
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("spectator")) {
				plugin.match.registerPlayer(player, "SPECTATOR");
				p.setGameMode(GameMode.SPECTATOR);
				return true;
			} else if (args.length == 1 && mode.equals(UHCGameMode.TEAM)) {
				String team = args[0].toString().toUpperCase();
				if (!plugin.scoreboardHandler.teamExists(team)) {
					p.sendMessage(LIB_NO_SUCH_TEAM);
					return true;
				}
				if (plugin.scoreboardHandler.isTeamFull(team)) {
					p.sendMessage(LIB_TEAM_FULL);
					return true;
				}
				plugin.match.registerPlayer(player, team);
				plugin.scoreboardHandler.forceHealthUpdates();
				p.sendMessage(LIB_TEAM_JOIN.replace("%t", team));
				return true;
			} else {
				showCommandMenu(p);
			}
		}
		return true;
	}
}

