package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FixSpectatorRunnable extends BukkitRunnable {
	
	Player player;
	boolean state;
	
	public FixSpectatorRunnable() { }
	
	public void setState(Player p, boolean state) {
		this.player = p;
		this.state = state;
	}

	@Override
	public void run() {
		player.setAllowFlight(state);
		player.setFlying(state);
		this.player = null;
	}

}
