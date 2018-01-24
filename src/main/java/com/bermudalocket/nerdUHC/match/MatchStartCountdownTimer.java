package com.bermudalocket.nerdUHC.match;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCSound;

public class MatchStartCountdownTimer extends BukkitRunnable {
	
	private UHCMatch match;
	private Set<UUID> players;
	int i = 10;
	
	public MatchStartCountdownTimer(UHCMatch match) {
		this.match = match;
		this.players = match.getPlayers();
	}
	
	public void tick(int i) {
		for (UUID uuid : players) {
			Player p = Bukkit.getPlayer(uuid);
			if (p == null) continue;
			p.sendTitle(ChatColor.RED + "The UHC starts in", i + " seconds", 2, 16, 2);
		}
		UHCSound.TIMERTICK.playSound();
	}

	public void run() {
		if (i == 0) {
			match.beginMatch();
			this.cancel();
		} else {
			tick(i);
		}
		i--;
	}

}