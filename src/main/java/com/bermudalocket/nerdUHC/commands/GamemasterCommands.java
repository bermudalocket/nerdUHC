package com.bermudalocket.nerdUHC.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCLibrary;

public class GamemasterCommands implements CommandExecutor {

	private final NerdUHC plugin;
	
	public GamemasterCommands(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) return false;
		
		Player p = (Player) sender;
		UHCMatch match = plugin.matchHandler.getMatch();

		if (cmd.getName().equalsIgnoreCase("shrinkwb")) {
			if (match.getWorldBorder().isShrinking()) {
				match.getWorldBorder().shrink();
			} else {
				match.getWorldBorder().freeze();
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("extendtime")) {
			if (args.length != 1) return false;
			
			try {
				int sec = Integer.valueOf(args[0]);
				match.getMatchTimer().extend(sec);
			} catch (Exception f) {
				return false;
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("togglepvp")) {
			if (plugin.CONFIG.WORLD.getPVP()) {
				plugin.CONFIG.WORLD.setPVP(false);
				UHCLibrary.LIB_PVP_DISABLED.tell(p);
			} else {
				plugin.CONFIG.WORLD.setPVP(true);
				UHCLibrary.LIB_PVP_ENABLED.tell(p);
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("sb-all")) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.setScoreboard(match.getScoreboard());
				UHCLibrary.LIB_SCOREBOARD_ALL_REFRESHED.tell(p);
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
					}
					return true;
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("uhc")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("stop")) {
					if (match.getMatchState() != UHCMatchState.PREGAME) {
						match.endMatch();
						plugin.matchHandler.getNewMatch();
						plugin.matchHandler.getMatch().migratePlayers();
					} else {
						UHCLibrary.LIB_ERR_NO_UHC_RUNNING.err(p);
					}
					return true;
				}
			}
		}

		return false;
		
	}
	
}
