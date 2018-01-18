package com.bermudalocket.nerdUHC.match;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.events.MatchTimerTickEvent;

public class MatchStartCountdownTimer {
	
	private NerdUHC plugin;
	
	public MatchStartCountdownTimer(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	public void run() {
		StartUHCTask.runTaskTimer(plugin, 1, 20);
	}
	
	public void tick(int i) {
		Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + "The UHC starts in",
				i + " seconds", 2, 16, 2));
		plugin.call(new MatchTimerTickEvent());
	}

	BukkitRunnable StartUHCTask = new BukkitRunnable() {
		int i = 10;
	
		@Override
		public void run() {
			if (i == 0) {
				plugin.match.startUHC();
				this.cancel();
			} else {
				tick(i);
			}
			i--;
		}
	};

}