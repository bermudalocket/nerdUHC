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

public class TeamLeaveExecutor extends CommandHandler {
	
	public TeamLeaveExecutor() {
		super ("leave", "help");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (NerdUHC.getGameMode().equals(UHCGameMode.SOLO)) {
			sender.sendMessage(ChatColor.RED + "This UHC is in solo mode! What are you even trying to do?");
			return true;
		}
		if (NerdUHC.isGameStarted()) {
			sender.sendMessage(ChatColor.RED + "You can't just abandon your team like that!");
			return true;
		} else if (args.length != 1) {
			showCommandMenu(sender);
			return true;
		} else if (!NerdUHC.isGameStarted() && NerdUHC.scoreboardHandler.getPlayerTeam((Player) sender) != null) {
			String team = args[0].toString().toUpperCase();
			if (NerdUHC.scoreboardHandler.getPlayerTeam((Player) sender).getName().equalsIgnoreCase(team)) {
				NerdUHC.scoreboardHandler.removePlayerTeam((Player) sender);
				sender.sendMessage(ChatColor.GRAY + "You have been removed from " + team);
			} else {
				sender.sendMessage(ChatColor.RED + "You're not even on that team!");
			}
		}
		return true;
	}

}
