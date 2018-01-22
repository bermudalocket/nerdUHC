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

	public ScoreboardTimer(UHCMatch match) {
		this.match = match;
	}

	@Override
	public void run() {
		
		if (match.isFrozen()) match.extendTime(1);

		nexttime = match.getDuration() - System.currentTimeMillis();
		timedisplay = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(nexttime),
				TimeUnit.MILLISECONDS.toMinutes(nexttime)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(nexttime)),
				TimeUnit.MILLISECONDS.toSeconds(nexttime)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(nexttime)));

		if (nexttime <= 600000) color = ChatColor.RED;

		try {
			sec = TimeUnit.MILLISECONDS.toSeconds(nexttime);
			match.getScoreboard().getObjective("main").setDisplayName(color + "" + ChatColor.BOLD + timedisplay);
			if (sec == 0 || sec < 0) {
				match.beginMatchEndTransition();
				this.cancel();
			}
		} catch (Exception e) {
			// small hiccup, objective just happened to get unregistered
		}
	}


}
