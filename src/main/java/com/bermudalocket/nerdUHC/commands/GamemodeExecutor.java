package com.bermudalocket.nerdUHC.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.Match.UHCGameMode;

public class GamemodeExecutor extends CommandHandler {
	
	private NerdUHC plugin;
	
	public GamemodeExecutor(NerdUHC plugin) {
		super("uhcmode", "solo", "team", "help");
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		
		String LIB_UPDATED = ChatColor.GRAY + "Game mode updated!";
		String LIB_ERR_INVALID = ChatColor.RED + "Invalid game mode!";
		
		
		if (args.length == 1) {
			String mode = args[0].toUpperCase();
			
			if (plugin.CONFIG.isValidGameMode(mode)) {					
				plugin.match.setGameMode(UHCGameMode.valueOf(mode));
				plugin.CONFIG.reload();
				sender.sendMessage(LIB_UPDATED);
			} else {
				sender.sendMessage(LIB_ERR_INVALID);
			}
			
		} else {
			showCommandMenu(sender);
		}
		return true;
	}
}
