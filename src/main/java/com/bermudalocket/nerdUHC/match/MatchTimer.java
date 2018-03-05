package com.bermudalocket.nerdUHC.match;

import java.util.concurrent.TimeUnit;

import com.bermudalocket.nerdUHC.modules.UHCLibrary;
import com.bermudalocket.nerdUHC.modules.UHCSound;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.modules.UHCMatch;

public class MatchTimer extends BukkitRunnable {

	private final UHCMatch match;
	private long duration;

	// tasks
	private boolean taskEnablePVP = false;
	private boolean taskWorldBorderShrink = false;
	
	// -------------------------------------------------------------------------------

	public MatchTimer(UHCMatch match, long duration) {
		this.match = match;
		this.duration = duration;
	}
	
	// -------------------------------------------------------------------------------

	public void extend(int sec) {
		duration += sec;
	}
	
	// -------------------------------------------------------------------------------

	@Override
	public void run() {

		duration--;

		if (!taskWorldBorderShrink && duration <= 60*60) {
			UHCLibrary.LIB_BORDER_SHRINKING.sendAsTitle();
			match.getWorldBorder().shrink();
			UHCSound.MATCHSTART.playSound();
			taskWorldBorderShrink = true;
		}

		if (taskWorldBorderShrink) {
			match.getScoreboardHandler().refresh();
		}

		if (!taskEnablePVP && match.getWorld().getTime() < 100) {
			UHCLibrary.LIB_PVP_ENABLED.sendAsTitle();
			UHCSound.MATCHSTART.playSound();
			match.getWorld().setPVP(true);
			taskEnablePVP = true;
			match.getScoreboardHandler().refresh();
		}

		if (duration <= 0) {
			match.beginDeathmatch();
			this.cancel();
			return;
		}

		long h = TimeUnit.SECONDS.toHours(duration);
		long m = TimeUnit.SECONDS.toMinutes(duration) - 60 * h;
		long s = duration - 60 * m - 60 * 60 * h;

		ChatColor color = (duration < 5*60) ? ChatColor.RED : ChatColor.WHITE;

		String title = String.format("%s%s%02d:%02d:%02d", color, ChatColor.BOLD, h, m, s);

		try {
			match.getScoreboard().getObjective("main").setDisplayName(title);
		} catch (Exception f) {
			// hiccup
		}

	}

}
