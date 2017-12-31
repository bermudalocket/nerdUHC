package com.bermudalocket.nerdUHC.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;

public class BarrierExecutor extends CommandHandler {
	
	public BarrierExecutor() {
		super("barrier", "on", "off", "help");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		Player player = (Player) sender;
		if (args[0].equalsIgnoreCase("on")) {
			player.sendMessage("Drawing barrier, please wait");
			NerdUHC.drawBarrier(player.getWorld(), 
					NerdUHC.CONFIG.SPAWN_X,
					NerdUHC.CONFIG.SPAWN_Y,
					NerdUHC.CONFIG.SPAWN_Z,
					NerdUHC.CONFIG.SPAWN_BARRIER_RADIUS,
					NerdUHC.CONFIG.SPAWN_BARRIER_BLOCK,
					Material.AIR);
			player.sendMessage("Barrier drawn!");
		} else if (args[0].equalsIgnoreCase("off")) {
			player.sendMessage("Removing barrier, please wait");
			NerdUHC.drawBarrier(player.getWorld(), 
					NerdUHC.CONFIG.SPAWN_X,
					NerdUHC.CONFIG.SPAWN_Y,
					NerdUHC.CONFIG.SPAWN_Z,
					NerdUHC.CONFIG.SPAWN_BARRIER_RADIUS,
					Material.AIR,
					NerdUHC.CONFIG.SPAWN_BARRIER_BLOCK);
			player.sendMessage("Barrier removed!");
		} else {
			showCommandMenu(sender);
		}
		return true;
	}
}

