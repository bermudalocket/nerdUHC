package com.bermudalocket.nerdUHC.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;

public class ConfigReloadExecutor extends CommandHandler {
	
	public ConfigReloadExecutor() {
		super("uhcreload", "help");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1 && args[0].equalsIgnoreCase("help")) return false;
		if (!(sender instanceof Player)) {
			sender.sendMessage("Consoles don't play UHCs!");
			return true;
		}
		if (NerdUHC.isGameStarted()) {
			sender.sendMessage("You can't reload the config while a UHC is in session.");
		} else {
			NerdUHC.CONFIG.reload();
			NerdUHC.scoreboardHandler.reloadScoreboards();
			Bukkit.getOnlinePlayers().forEach(player -> NerdUHC.registerPlayer(player, true));
		}
		return true;
	}
}

