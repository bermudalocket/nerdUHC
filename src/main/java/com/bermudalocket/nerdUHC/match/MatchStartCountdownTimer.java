package com.bermudalocket.nerdUHC.match;

import com.bermudalocket.nerdUHC.NerdUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCSound;

public class MatchStartCountdownTimer extends BukkitRunnable {
	
	private final UHCMatch match;
	
	private boolean isRunning = false;
	private int i = 10;
	
	public MatchStartCountdownTimer(UHCMatch match) {
		this.match = match;
	}
	
	public boolean isRunning() {
		return isRunning;
	}

	public void start() {
		isRunning = true;
		this.runTaskTimer(NerdUHC.plugin, 1, 20);
	}

	public void pause() {
		isRunning = false;
	}

	public void resume() {
		isRunning = true;
	}
	
	private void tick(int i) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendTitle(ChatColor.RED + "The UHC starts in", i + " seconds", 2, 16, 2);
		}
		UHCSound.TIMERTICK.playSound();
	}

	public void run() {
		if (!isRunning) return;
		
		if (i == 0) {
			match.beginMatch();
			this.cancel();
		} else {
			tick(i);
		}
		i--;
	}
}