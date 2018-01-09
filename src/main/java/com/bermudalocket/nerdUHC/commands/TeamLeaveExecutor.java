package com.bermudalocket.nerdUHC.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.Match.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;

public class TeamLeaveExecutor extends CommandHandler {
	
	private NerdUHC plugin;
	
	public TeamLeaveExecutor(NerdUHC plugin) {
		super ("leave", "help");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		
		UUID player = ((Player) sender).getUniqueId();
		UHCPlayer p = plugin.match.getPlayer(player);
		
		String LIB_ERR_SOLO = ChatColor.RED + "This UHC is in solo mode! What are you even trying to do?";
		String LIB_ERR_STARTED = ChatColor.RED + "You can't just abandon your team like that!";
		String LIB_ERR_NOTEAM = ChatColor.RED + "You aren't on a team!";
		String LIB_REMOVED = ChatColor.GRAY + "You have left your team.";
		
		if (plugin.match.getGameMode().equals(UHCGameMode.SOLO)) {
			sender.sendMessage(LIB_ERR_SOLO);
			return true;
		} else if (plugin.match.isGameStarted()) {
			sender.sendMessage(LIB_ERR_STARTED);
			return true;
		} else if (args.length != 0) {
			showCommandMenu(sender);
			return true;
		} else if (p.getTeam() == null) {
			sender.sendMessage(LIB_ERR_NOTEAM);
			return true;
		} else {
			if (p.getTeam().equalsIgnoreCase("SPECTATOR")) ((Player) sender).setGameMode(GameMode.SURVIVAL);
			p.setTeam(null);
			sender.sendMessage(LIB_REMOVED);
			return true;
		}
	}

}
