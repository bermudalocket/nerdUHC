package com.bermudalocket.nerdUHC.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.NerdUHC.UHCGameMode;

/////////////////////////////////////////////////////////////////////////////
//
//	Team Change Executor
//
//

public class TeamJoinExecutor extends CommandHandler {
	
	// ********************************************
	// registers subcommands
	// ********************************************
	public TeamJoinExecutor() {
		super("join", "help");
	}
	
	// ********************************************
	//		/uhcteam [join|leave|list]
	// ********************************************
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (NerdUHC.isGameStarted()) return true;
		
		if (NerdUHC.getGameMode().equals(UHCGameMode.SOLO)) {
			sender.sendMessage(ChatColor.RED + "This UHC is a solo one... what are you even trying to do?");
			return true;
		}
	
		String team = args[0].toString().toUpperCase();
		if (NerdUHC.scoreboardHandler.teamExists(team) && NerdUHC.scoreboardHandler.getPlayerTeam((Player) sender) == null) {
			if (NerdUHC.scoreboardHandler.getTeamSize(team) < NerdUHC.CONFIG.MAX_TEAM_SIZE) {
				Player player = (Player) sender;
				NerdUHC.scoreboardHandler.setPlayerTeam(player, team);
				sender.sendMessage(ChatColor.GRAY + "You have been added to team " + team);
			} else {
				sender.sendMessage(ChatColor.RED + "That team is full :( Try choosing a different team.");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "That team doesn't exist or you're already on a team.");
		}
		return true;
	}
}

