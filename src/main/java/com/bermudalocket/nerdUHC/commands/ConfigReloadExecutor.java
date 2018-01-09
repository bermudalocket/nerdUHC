package com.bermudalocket.nerdUHC.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import com.bermudalocket.nerdUHC.NerdUHC;

public class ConfigReloadExecutor extends CommandHandler {
	
	private NerdUHC plugin;
	

	public ConfigReloadExecutor(NerdUHC plugin) {
		super("uhcreload", "help");
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		
		String LIB_ERR_STARTED = ChatColor.RED + "You can't reload the config while a UHC is in session.";
		
		if (plugin.match.isGameStarted()) {
			sender.sendMessage(LIB_ERR_STARTED);
		} else {
			plugin.CONFIG.reload();
		}
		return true;
	}
}

