package com.bermudalocket.nerdUHC.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.NerdUHC.UHCGameMode;
import com.bermudalocket.nerdUHC.ScoreboardHandler;

public class NerdUHCCommandExecutor implements CommandExecutor {
	
	private NerdUHC plugin;
	
	public NerdUHCCommandExecutor(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	public void showCommandMenu(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "/nerduhc subcommands:");
		sender.sendMessage(ChatColor.GRAY + "================================================");
		sender.sendMessage("/nerduhc mode solo : sets the next UHC's mode to solo");
		sender.sendMessage("/nerduhc mode team : sets the next UHC's mode to team");
		sender.sendMessage(ChatColor.GRAY + "================================================");
		sender.sendMessage("/nerduhc barrier on : encloses a small area around the spawn coordinates with glass");
		sender.sendMessage("/nerduhc barrier off : removes the glass barrier around the spawn coordinates");
		sender.sendMessage(ChatColor.GRAY + "================================================");
		sender.sendMessage("/nerduhc forfeit <player> : removes a player from the UHC");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Consoles don't play UHCs.");
			return true;
		}
		if (command.getName().equalsIgnoreCase("nerduhc")) {		// /nerduhc
			if (args.length == 0) {
				showCommandMenu(sender);
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {		// /nerduhc reload
				if (plugin.isGameStarted()) {
					sender.sendMessage("You can't reload the config while a UHC is in session.");
				} else {
					plugin.CONFIG.reload();
					plugin.reloadScoreboards();
					Bukkit.getOnlinePlayers().forEach(player -> plugin.registerPlayer(player, true));
				}
				return true;
			} else if (args.length == 2 && args[0].equalsIgnoreCase("mode")) {		// /nerduhc mode													// /nerduhc mode *
				if (plugin.isValidGameMode(args[1].toUpperCase())) {					// where * in enum
					plugin.setGameMode(UHCGameMode.valueOf(args[1].toUpperCase()));
					sender.sendMessage("Game mode updated! You must refresh configuration with /nerduhc reload, before any changes will take effect.");
				} else {
					sender.sendMessage("Invalid game mode!");
				}
				return true;
			} else if (args.length == 2 && args[0].equalsIgnoreCase("barrier")) {
				Player player = (Player) sender;
				if (args[1].equalsIgnoreCase("on")) {
					player.sendMessage("Drawing barrier, please wait");
					plugin.drawBarrier(player.getWorld(), 
							plugin.CONFIG.SPAWN_X,
							plugin.CONFIG.SPAWN_Y,
							plugin.CONFIG.SPAWN_Z,
							plugin.CONFIG.SPAWN_BARRIER_RADIUS,
							plugin.CONFIG.SPAWN_BARRIER_BLOCK,
							Material.AIR);
					player.sendMessage("Barrier drawn!");
				} else if (args[1].equalsIgnoreCase("off")) {
					player.sendMessage("Removing barrier, please wait");
					plugin.drawBarrier(player.getWorld(), 
							plugin.CONFIG.SPAWN_X,
							plugin.CONFIG.SPAWN_Y,
							plugin.CONFIG.SPAWN_Z,
							plugin.CONFIG.SPAWN_BARRIER_RADIUS,
							Material.AIR,
							plugin.CONFIG.SPAWN_BARRIER_BLOCK);
					player.sendMessage("Barrier removed!");
				} else {
					
				}
				return true;
			} else if (args.length == 2 && args[0].equalsIgnoreCase("forfeit")) {
				
				return true;
			} else {
				showCommandMenu(sender);
				return true;
			}
				
				
				
			
		} // if /uhc
		return false;
	} // onCommand
} // nerdUHCCommandExecutor
