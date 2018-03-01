package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;

class FixSpectatorRunnable extends BukkitRunnable {
	
	private final NerdUHC plugin;
	
	private Player player;
	private boolean state;
	
	FixSpectatorRunnable(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	public void setState(Player p, boolean state) {
		this.player = p;
		this.state = state;
		this.runTaskLater(plugin, 1);
	}

	@Override
	public void run() {
		player.setAllowFlight(state);
		player.setFlying(state);
		this.player = null;
	}

}
