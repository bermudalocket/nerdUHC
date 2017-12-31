package com.bermudalocket.nerdUHC.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import com.bermudalocket.nerdUHC.NerdUHC;

/////////////////////////////////////////////////////////////////////////////
//
//	Barrier Executor
//
//

public class BarrierExecutor extends CommandHandler {
	
	// ********************************************
	// register subcommands
	// ********************************************
	public BarrierExecutor() {
		super("barrier", "on", "off", "help");
	}
	
	// ********************************************
	// 		/barrier [on|off]
	// ********************************************
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		Player player = (Player) sender;
		if (args[0].equalsIgnoreCase("on")) {
			player.sendMessage(ChatColor.GRAY + "Drawing barrier, please wait...");
			NerdUHC.drawBarrier(player.getWorld(), 
					NerdUHC.CONFIG.SPAWN_X,
					NerdUHC.CONFIG.SPAWN_Y,
					NerdUHC.CONFIG.SPAWN_Z,
					NerdUHC.CONFIG.SPAWN_BARRIER_RADIUS,
					NerdUHC.CONFIG.SPAWN_BARRIER_BLOCK,
					Material.AIR);
			player.sendMessage(ChatColor.GRAY + "Barrier drawn!");
		} else if (args[0].equalsIgnoreCase("off")) {
			player.sendMessage(ChatColor.GRAY + "Removing barrier, please wait...");
			NerdUHC.drawBarrier(player.getWorld(), 
					NerdUHC.CONFIG.SPAWN_X,
					NerdUHC.CONFIG.SPAWN_Y,
					NerdUHC.CONFIG.SPAWN_Z,
					NerdUHC.CONFIG.SPAWN_BARRIER_RADIUS,
					Material.AIR,
					NerdUHC.CONFIG.SPAWN_BARRIER_BLOCK);
			player.sendMessage(ChatColor.GRAY + "Barrier removed!");
		} else {
			showCommandMenu(sender);
		}
		return true;
	}
}

