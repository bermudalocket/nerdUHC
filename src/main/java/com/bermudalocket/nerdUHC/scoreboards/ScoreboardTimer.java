package com.bermudalocket.nerdUHC.scoreboards;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.modules.UHCMatch;

public class ScoreboardTimer extends BukkitRunnable {

	private UHCMatch match;

	private long duration;
	private long h;
	private long m;
	private long s;
	
	private String title;
	private ChatColor color = ChatColor.WHITE;
	
	// -------------------------------------------------------------------------------

	public ScoreboardTimer(UHCMatch match, long duration) {
		this.match = match;
		this.duration = duration;
	}
	
	// -------------------------------------------------------------------------------

	public void extend(int sec) {
		duration += sec;
	}
	
	public void setDuration(int sec) {
		this.duration = sec * 60;
	}

	@Override
	public void run() {

		if (this.isCancelled())
			return;

		duration--;

		if (duration == 0 || duration < 0) {
			match.beginMatchEndTransition();
		}

		h = TimeUnit.SECONDS.toHours(duration);
		m = TimeUnit.SECONDS.toMinutes(duration) - 60 * h;
		s = duration - 60 * m - 60 * 60 * h;

		if ((duration / 60) < 5) {
			color = ChatColor.RED;
		} else {
			color = ChatColor.WHITE;
		}

		title = null;

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
		case PREGAME:
			title = String.format("%s NerdUHC", ChatColor.BOLD);
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
