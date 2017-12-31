package com.bermudalocket.nerdUHC.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.NerdUHC.UHCGameMode;

public class GamemodeExecutor extends CommandHandler {
	
	public GamemodeExecutor() {
		super("uhcmode", "solo", "team", "help");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			if (NerdUHC.isValidGameMode(args[0].toUpperCase())) {					
				NerdUHC.setGameMode(UHCGameMode.valueOf(args[0].toUpperCase()));
				sender.sendMessage("Game mode updated! You must refresh configuration with /nerduhc reload, before any changes will take effect.");
			} else {
				sender.sendMessage("Invalid game mode!");
			}
		} else {
			sender.sendMessage("You have to specify a mode.");
		}
		return true;
	}
}

