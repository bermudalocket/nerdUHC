package com.bermudalocket.nerdUHC.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCSound;
import com.bermudalocket.nerdUHC.modules.UHCLibrary;

public class GamemasterCommands implements CommandExecutor {

	private NerdUHC plugin;
	
	public GamemasterCommands(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) return false;
		
		Player p = (Player) sender;
		UHCMatch match = plugin.matchHandler.getMatchByPlayer(p);
		if (match == null) return true;
		
		if (cmd.getName().equalsIgnoreCase("sb-all")) {
			for (UUID uuid : match.getPlayers()) {
				Player player = Bukkit.getPlayer(uuid);
				if (player == null) continue;
				player.setScoreboard(match.getScoreboard());
				UHCLibrary.LIB_SCOREBOARD_ALL_REFRESHED.get(p);
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("freeze")) {
			if (match.getMatchState() == UHCMatchState.INPROGRESS || match.getMatchState() == UHCMatchState.DEATHMATCH) {
				if (match.isFrozen()) {
					UHCLibrary.LIB_UNFROZEN.get(p);
				} else {
					UHCLibrary.LIB_FROZEN.get(p);
				}
				match.freeze(); //unfreeze
			} else {
				UHCLibrary.LIB_ERR_NO_UHC_RUNNING.err(p);
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("barrier")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("on")) {
				match.getBarrier().drawBarrier(true);
				UHCLibrary.LIB_BARRIER_DRAWN.get(p);
			} else if (args.length == 1 && args[0].equalsIgnoreCase("off")) {
				match.getBarrier().drawBarrier(false);
				UHCLibrary.LIB_BARRIER_REM.get(p);
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("start")) {
					if (match.getMatchState().equals(UHCMatchState.PREGAME)) {
						match.beginMatchStartCountdown();
					} else {
						UHCLibrary.LIB_ERR_UHC_RUNNING.err(p);
						UHCSound.OOPS.playSound(p);
					}
					return true;
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("stop")) {
					if (match.getMatchState() != UHCMatchState.PREGAME) {
						match.beginMatchEndTransition();
					} else {
						UHCLibrary.LIB_ERR_UHC_RUNNING.err(p);
						UHCSound.OOPS.playSound(p);
					}
					return true;
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					plugin.CONFIG.reload();
					UHCLibrary.LIB_CONF_RELOADED.get(p);
					return true;
				}
			}
		}

		return false;
		
	}
	
}
