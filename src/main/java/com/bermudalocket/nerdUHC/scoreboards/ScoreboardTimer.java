package com.bermudalocket.nerdUHC.scoreboards;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.modules.UHCMatch;

public class ScoreboardTimer extends BukkitRunnable {
	
	private UHCMatch match;
	String timedisplay;
	ChatColor color = ChatColor.WHITE;
	long nexttime;
	long sec;
	
	long duration;
	long h;
	long m;
	long s;

	public ScoreboardTimer(UHCMatch match, int duration) {
		this.match = match;
		this.duration = duration * 60; // convert minutes to seconds
	}
	
	public void extend(int sec) {
		duration += sec;
	}

	@Override
	public void run() {
		
		if (this.isCancelled()) return;
		if (match.isFrozen()) return;
		
		duration--;
		
		if (duration == 0 || duration < 0) {
			match.beginMatchEndTransition();
			
		}
		
		h = TimeUnit.SECONDS.toHours(duration);
		m = TimeUnit.SECONDS.toMinutes(duration) - 60*h;
		s = duration - 60*(h + m);
		
		if ((duration/60) < 5) {
			color = ChatColor.RED;
		} else {
			color = ChatColor.WHITE;
		}
		
		String title = null;
		
		switch (match.getMatchState()) {
			case DEATHMATCH:
				title = String.format("%s%s%s Deathmatch!", ChatColor.RED, ChatColor.BOLD, ChatColor.ITALIC);
				break;
			case END:
				title = String.format("%s Postgame", ChatColor.GREEN);
				break;
			case FROZEN:
				title = String.format("%s Frozen", ChatColor.AQUA);
				break;
			case INPROGRESS:
				title = String.format("%s%s%02d:%02d:%02d", color, ChatColor.BOLD, h, m, s);
				break;
			case LAST:
				break;
			case PREGAME:
				title = String.format("%s NerdUHC", ChatColor.BOLD);
				break;
			case TRANSITION:
				break;
			default:
				title = String.format("%s NerdUHC", ChatColor.BOLD);
				break;	
		}
		
		try {
			match.getScoreboard().getObjective("main").setDisplayName(title);
		} catch (Exception f) {
			// hiccup
		}

	}


}
