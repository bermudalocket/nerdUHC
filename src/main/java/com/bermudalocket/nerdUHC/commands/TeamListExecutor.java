package com.bermudalocket.nerdUHC.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Set;

import org.bukkit.ChatColor;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.Match.UHCGameMode;

public class TeamListExecutor extends CommandHandler {
	
	private NerdUHC plugin;
	
	public TeamListExecutor(NerdUHC plugin) {
		super ("teamlist", "help");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		
		UHCGameMode mode = plugin.match.getGameMode();
		Set<String> teams = plugin.scoreboardHandler.getTeams();
		
		String LIB_ERR_SOLO = ChatColor.RED + "This UHC is in solo mode! What are you even trying to do?";
		String LIB_SPEC = ChatColor.GRAY + "SPECTATOR";
		
		if (mode.equals(UHCGameMode.SOLO)) {
			sender.sendMessage(LIB_ERR_SOLO);
			return true;
		}
		
		teams.forEach(team -> {
			ChatColor color = plugin.scoreboardHandler.getTeamColor(team);
			sender.sendMessage(color + team);
		});
		sender.sendMessage(LIB_SPEC);
		return true;
	}
}
