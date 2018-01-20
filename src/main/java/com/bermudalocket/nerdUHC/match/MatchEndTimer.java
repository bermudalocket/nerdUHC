package com.bermudalocket.nerdUHC.match;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;
import com.bermudalocket.nerdUHC.modules.UHCSound;

public class MatchEndTimer {
	
	private NerdUHC plugin;
	private List<UHCPlayer> winnerlist;
	private int winnerdisplaytime;
	private UHCPlayer currentwinner;
	
	public MatchEndTimer(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	public void run(List<UHCPlayer> winners) {
		
		this.winnerlist = winners;
		if (winnerlist.size() > 0) {
			this.winnerdisplaytime = Math.floorDiv(300, winnerlist.size()) - 20;
		} else {
			this.winnerdisplaytime = 200;
		}
		
		TimesUp.runTask(plugin);
		
		AndTheWinnerIs.runTaskLater(plugin, 100);
		
		int i = 0;
		for (UHCPlayer w : winnerlist) {
			currentwinner = w;
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
			plugin.match.stopUHC();
		}
	};

}