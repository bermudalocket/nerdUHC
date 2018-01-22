package com.bermudalocket.nerdUHC.match;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCSound;

public class MatchEndTimer {
	
	private NerdUHC plugin;
	private UHCMatch match;
	
	private Set<Player> winnerlist;
	private Player currentwinner;
	private int winnerdisplaytime;
	
	public MatchEndTimer(NerdUHC plugin, UHCMatch match) {
		this.plugin = plugin;
		this.match = match;
	}
	
	public void run(Set<Player> winners) {
		this.winnerlist = winners;
		if (winnerlist.size() > 0) {
			this.winnerdisplaytime = Math.floorDiv(300, winnerlist.size()) - 20;
		} else {
			this.winnerdisplaytime = 200;
		}
		
		TimesUp.runTask(plugin);
		
		AndTheWinnerIs.runTaskLater(plugin, 100);
		
		int i = 0;
		for (Player p : winnerlist) {
			currentwinner = p;
			Winner.runTaskLater(plugin, (200 + (i*winnerdisplaytime)));
			i++;
		}
		
		End.runTaskLater(plugin, 480);
	}

	BukkitRunnable TimesUp = new BukkitRunnable() {
		@Override
		public void run() {
			Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle("Time's up!", null, 10, 80, 10));
		}
	};
	
	BukkitRunnable AndTheWinnerIs = new BukkitRunnable() {
		@Override
		public void run() {
			if (winnerlist.size() == 1) {
				Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle("And the winner is...", null, 10, 80, 10));
			} else {
				Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle("And the winners are...", null, 10, 80, 10));
			}
		}
	};
	
	BukkitRunnable Winner = new BukkitRunnable() {
		@Override
		public void run() {
			UHCSound.MATCHEND.playSound();
			Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle(currentwinner.getDisplayName(), null, 10, winnerdisplaytime, 10));
		}
	};
	
	BukkitRunnable End = new BukkitRunnable() {
		@Override
		public void run() {
			winnerlist.clear();
			currentwinner = null;
			match.next();
		}
	};

}