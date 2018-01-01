package com.bermudalocket.nerdUHC.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.NerdUHC.UHCGameMode;

/////////////////////////////////////////////////////////////////////////////
//
//	GameMode Executor
//
//

public class GamemodeExecutor extends CommandHandler {
	
	// ********************************************
	// register subcommands
	// ********************************************
	public GamemodeExecutor() {
		super("uhcmode", "solo", "team", "help");
	}
	
	// ********************************************
	//			/uhcmode [mode]
	// will dynamically handle any new modes
	// as long as they are defined in the
	// UHCGameMode enum in NerdUHC.java
	// ********************************************
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			if (NerdUHC.isValidGameMode(args[0].toUpperCase())) {					
				NerdUHC.setGameMode(UHCGameMode.valueOf(args[0].toUpperCase()));
				sender.sendMessage(ChatColor.GRAY + "Game mode updated! You must refresh configuration with " + ChatColor.WHITE + "/uhcreload" + ChatColor.GRAY + " before any changes will take effect.");
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid game mode!");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You have to specify a mode.");
		}
		return true;
	}
}
