package com.bermudalocket.nerdUHC.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.NerdUHC.UHCGameMode;

public class TeamChangeExecutor extends CommandHandler {
	
	public TeamChangeExecutor() {
		super("uhcteam", "join", "leave", "list", "help");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (NerdUHC.isGameStarted()) return true;
		
		if (NerdUHC.getGameMode().equals(UHCGameMode.SOLO)) {
			sender.sendMessage("This UHC is a solo one... what are you even trying to do?");
			return true;
		}
		
		if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
			String team = args[1].toString().toUpperCase();
			if (NerdUHC.scoreboardHandler.teamExists(team) && NerdUHC.scoreboardHandler.getPlayerTeam((Player) sender) == null) {
				if (NerdUHC.scoreboardHandler.getTeamSize(team) < NerdUHC.CONFIG.MAX_TEAM_SIZE) {
					NerdUHC.scoreboardHandler.setPlayerTeam((Player) sender, team);
					sender.sendMessage("You have been added to team " + team);
				} else {
					sender.sendMessage("That team is full :( Try choosing a different team.");
				}
			} else {
				sender.sendMessage("That team doesn't exist or you're already on a team.");
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("leave")) {
			if (NerdUHC.isGameStarted()) {
				sender.sendMessage("You can't just abandon your team like that!");
			} else if (!NerdUHC.isGameStarted() && NerdUHC.scoreboardHandler.getPlayerTeam((Player) sender) != null) {
				String team = args[1].toString().toUpperCase();
				if (team == NerdUHC.scoreboardHandler.getPlayerTeam((Player) sender).getName()) {
					NerdUHC.scoreboardHandler.removePlayerTeam((Player) sender);
					sender.sendMessage("You have been removed from " + team);
				} else {
					sender.sendMessage("You're not even on that team!");
				}
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
			sender.sendMessage("Teams: " + NerdUHC.scoreboardHandler.TEAMS.keySet().toString());
		} else {
			showCommandMenu(sender);
		}
		return true;
	}
}

