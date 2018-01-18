package com.bermudalocket.nerdUHC.match;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;

public class MatchEndTimer {
	
	private NerdUHC plugin;
	private Collection<? extends Player> players;
	private List<UHCPlayer> winnerlist;
	private int winnerdisplaytime;
	private UHCPlayer currentwinner;
	
	public MatchEndTimer(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	public void run(List<UHCPlayer> winners) {
		
		this.winnerlist = winners;
		this.winnerdisplaytime = Math.floorDiv(300, winnerlist.size()) - 20;
		
		TimesUp.runTask(plugin);
		
		AndTheWinnerIs.runTaskLater(plugin, 100);
		
		int i = 0;
		for (UHCPlayer w : winnerlist) {
			currentwinner = w;
			Winner.runTaskLater(plugin, (200 + (i*winnerdisplaytime)));
			i++;
		}
		
	}

	BukkitRunnable TimesUp = new BukkitRunnable() {
		@Override
		public void run() {
			players = Bukkit.getOnlinePlayers();
			players.forEach(p -> p.sendTitle("Time's up!", null, 10, 80, 10));
		}
	};
	
	BukkitRunnable AndTheWinnerIs = new BukkitRunnable() {
		@Override
		public void run() {
			players = Bukkit.getOnlinePlayers();
			if (winnerlist.size() == 1) {
				players.forEach(p -> p.sendTitle("And the winner is...", null, 10, 80, 10));
			} else {
				players.forEach(p -> p.sendTitle("And the winners are...", null, 10, 80, 10));
			}
		}
	};
	
	BukkitRunnable Winner = new BukkitRunnable() {
		@Override
		public void run() {
			players = Bukkit.getOnlinePlayers();
			players.forEach(p -> p.sendTitle(currentwinner.getColor() + currentwinner.getName(), null, 10, winnerdisplaytime, 10));
		}
	};

}