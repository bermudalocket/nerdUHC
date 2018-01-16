package com.bermudalocket.nerdUHC.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCGameMode;

public class GamemasterCommands implements CommandExecutor {

	private NerdUHC plugin;
	
	public GamemasterCommands(NerdUHC plugin) {
		this.plugin = plugin;
		plugin.getCommand("barrier").setExecutor(this);
		plugin.getCommand("uhc").setExecutor(this);
		plugin.getCommand("freeze").setExecutor(this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		String LIB_UPDATED = ChatColor.GRAY + "Game mode updated!";
		String LIB_UHC_STARTED = ChatColor.GRAY + "Starting a UHC...";
		String LIB_UHC_STOPPED = ChatColor.GRAY + "Stopping the UHC...";
		String LIB_CONF_RELOADED = ChatColor.GRAY + "Config reloaded!";
		String LIB_FROZEN = ChatColor.GRAY + "Players have been " + ChatColor.AQUA + "frozen" + ChatColor.GRAY + ".";
		String LIB_UNFROZEN = ChatColor.GRAY + "Players have been unfrozen.";
		
		String LIB_ERR_INVALID = ChatColor.RED + "Invalid game mode!";
		String LIB_ERR_UHC_RUNNING = ChatColor.RED + "A UHC is already in progress!";
		String LIB_ERR_NO_UHC_RUNNING = ChatColor.RED + "A UHC is not currently running!";
		String LIB_ERR_STARTED = ChatColor.RED + "You can't reload the config while a UHC is in session.";
		
		if (cmd.getName().equalsIgnoreCase("freeze")) {
			Boolean frozen = plugin.match.arePlayersFrozen();
			plugin.match.freezePlayers(!frozen);
			sender.sendMessage((frozen) ? LIB_UNFROZEN : LIB_FROZEN);
		}
		
		if (cmd.getName().equalsIgnoreCase("barrier")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("on")) {
				plugin.barrier.drawBarrier(true);
			} else if (args.length == 1 && args[0].equalsIgnoreCase("off")) {
				plugin.getLogger().info("barrier turning off");
				plugin.barrier.drawBarrier(false);
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("mode")) {
					String mode = args[1].toUpperCase();
					if (plugin.CONFIG.isValidGameMode(mode)) {					
						plugin.match.setGameMode(UHCGameMode.valueOf(mode));
						plugin.CONFIG.reload();
						sender.sendMessage(LIB_UPDATED);
					} else {
						sender.sendMessage(LIB_ERR_INVALID);
					}
					return true;
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("start")) {
					sender.sendMessage(plugin.match.startUHC() ? LIB_UHC_STARTED : LIB_ERR_UHC_RUNNING);
					return true;
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("stop")) {
					sender.sendMessage(plugin.match.stopUHC() ? LIB_UHC_STOPPED : LIB_ERR_NO_UHC_RUNNING);
					return true;
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					sender.sendMessage(reloadConfig() ? LIB_CONF_RELOADED : LIB_ERR_STARTED);
					return true;
				}
			}
		}

		return false;
		
	}
	
	public boolean reloadConfig() {
		if (plugin.match.isGameStarted()) {
			return false;
		} else {
			plugin.CONFIG.reload();
			plugin.scoreboardHandler.refreshScoreboard();
			return true;
		}
	}
	
}
