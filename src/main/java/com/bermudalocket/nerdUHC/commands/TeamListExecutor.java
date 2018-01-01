package com.bermudalocket.nerdUHC.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.NerdUHC.UHCGameMode;

/////////////////////////////////////////////////////////////////////////////
//
//	Team Change Executor
//
//

public class TeamListExecutor extends CommandHandler {
	
	public TeamListExecutor() {
		super ("teamlist", "help");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (NerdUHC.getGameMode().equals(UHCGameMode.SOLO)) {
			sender.sendMessage(ChatColor.RED + "This UHC is in solo mode! What are you even trying to do?");
			return true;
		}
		NerdUHC.scoreboardHandler.TEAMS.keySet().forEach(team -> {
			ChatColor color = NerdUHC.scoreboardHandler.getTeamColor(team);
			sender.sendMessage(color + team);
		});
		return true;
	}

}
