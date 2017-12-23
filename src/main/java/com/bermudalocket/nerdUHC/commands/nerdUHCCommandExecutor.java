package com.bermudalocket.nerdUHC.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.nerdUHC;

public class nerdUHCCommandExecutor implements CommandExecutor {
	
	private nerdUHC plugin;
	
	public nerdUHCCommandExecutor(nerdUHC plugin) {
		this.plugin = plugin;
		plugin.getCommand("uhc").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Consoles don't play UHCs.");
			return true;
		}
		if (command.getName().equalsIgnoreCase("uhc")) {
			switch (args[0].toLowerCase()) {
				case "mode":
					switch (args[1].toLowerCase()) {
						case "solo":
							plugin.setGameMode(1);
							sender.sendMessage("Game mode set to solo");
							break;
						case "team":
							plugin.setGameMode(2);
							sender.sendMessage("Game mode set to teams");
							break;
						default:
							sender.sendMessage(ChatColor.RED + "Invalid input.");
							break;
					}
					return true;
			case "barrier":
					switch (args[1].toLowerCase()) {
						case "on":
							break;
						case "off":
							break;
						default:
							sender.sendMessage(ChatColor.RED + "Invalid input.");
							break;
					}
					return true;
				case "forefeit":
					return true;
				default:
					sender.sendMessage(ChatColor.RED + "/uhc subcommands:");
					sender.sendMessage(ChatColor.GRAY + "================================================");
					sender.sendMessage("/uhc mode solo : sets the next UHC's mode to solo");
					sender.sendMessage("/uhc mode team : sets the next UHC's mode to team");
					sender.sendMessage(ChatColor.GRAY + "================================================");
					sender.sendMessage("/uhc barrier on : encloses a small area around the spawn coordinates with glass");
					sender.sendMessage("/uhc barrier off : removes the glass barrier around the spawn coordinates");
					sender.sendMessage(ChatColor.GRAY + "================================================");
					sender.sendMessage("/uhc forefeit <player> : removes a player from the UHC");
					return true;
			} // switch args[0]
		} // if /uhc
		return false;
	} // onCommand
} // nerdUHCCommandExecutor
