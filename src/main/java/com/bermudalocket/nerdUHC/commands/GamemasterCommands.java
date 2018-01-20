package com.bermudalocket.nerdUHC.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.events.MatchStateChangeEvent;
import com.bermudalocket.nerdUHC.modules.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCSound;

public class GamemasterCommands implements CommandExecutor {

	private NerdUHC plugin;
	
	static final String LIB_UPDATED = ChatColor.GRAY + "Game mode updated!";
	static final String LIB_UHC_STOPPED = ChatColor.GRAY + "Stopping the UHC...";
	static final String LIB_CONF_RELOADED = ChatColor.GRAY + "Config reloaded!";
	static final String LIB_FROZEN = ChatColor.GRAY + "Players have been " + ChatColor.AQUA + "frozen" + ChatColor.GRAY + ".";
	static final String LIB_UNFROZEN = ChatColor.GRAY + "Players have been unfrozen.";
	static final String LIB_BARRIER_DRAWN = ChatColor.GRAY + "Barrier drawn!";
	static final String LIB_BARRIER_REM = ChatColor.GRAY + "Barrier removed!";
	
	static final String LIB_ERR_INVALID = ChatColor.RED + "Invalid game mode!";
	static final String LIB_ERR_UHC_RUNNING = ChatColor.RED + "A UHC is already in progress!";
	static final String LIB_ERR_NO_UHC_RUNNING = ChatColor.RED + "A UHC is not currently running!";
	static final String LIB_ERR_STARTED = ChatColor.RED + "You can't reload the config while a UHC is in session.";
	
	public GamemasterCommands(NerdUHC plugin) {
		this.plugin = plugin;
		plugin.getCommand("barrier").setExecutor(this);
		plugin.getCommand("uhc").setExecutor(this);
		plugin.getCommand("freeze").setExecutor(this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("freeze")) {
			Boolean frozen = plugin.match.arePlayersFrozen();
			if (frozen) {
				plugin.call(new MatchStateChangeEvent(UHCMatchState.LAST));
				sender.sendMessage(LIB_UNFROZEN);
			} else {
				plugin.call(new MatchStateChangeEvent(UHCMatchState.FROZEN));
				sender.sendMessage(LIB_FROZEN);
			}
			plugin.match.freezePlayers(!frozen);
			sender.sendMessage((frozen) ? LIB_UNFROZEN : LIB_FROZEN);
		}
		
		if (cmd.getName().equalsIgnoreCase("barrier")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("on")) {
				plugin.barrier.drawBarrier(true);
				sender.sendMessage(LIB_BARRIER_DRAWN);
			} else if (args.length == 1 && args[0].equalsIgnoreCase("off")) {
				plugin.barrier.drawBarrier(false);
				sender.sendMessage(LIB_BARRIER_REM);
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
						UHCSound.OOPS.playSound((Player) sender);
					}
					return true;
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("start")) {
					if (plugin.match.getMatchState().equals(UHCMatchState.PREGAME)) {
						plugin.call(new MatchStateChangeEvent(UHCMatchState.INPROGRESS));
					} else {
						sender.sendMessage(LIB_ERR_UHC_RUNNING);
						UHCSound.OOPS.playSound((Player) sender);
					}
					return true;
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("stop")) {
					if (plugin.match.getMatchState().equals(UHCMatchState.INPROGRESS) || plugin.match.getMatchState().equals(UHCMatchState.DEATHMATCH)) {
						plugin.call(new MatchStateChangeEvent(UHCMatchState.END));
					} else {
						sender.sendMessage(LIB_ERR_UHC_RUNNING);
						UHCSound.OOPS.playSound((Player) sender);
					}
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
			plugin.getNewScoreboardHandler();
			return true;
		}
	}
	
}
