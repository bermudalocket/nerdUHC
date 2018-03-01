package com.bermudalocket.nerdUHC.match;

import java.util.concurrent.TimeUnit;

import com.bermudalocket.nerdUHC.modules.UHCLibrary;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.modules.UHCMatch;

@SuppressWarnings("ALL")
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
			match.getWorldBorder().shrink();
			taskWorldBorderShrink = true;
		}

		if (!taskEnablePVP && match.getWorld().getTime() < 100) {
			UHCLibrary.LIB_PVP_ENABLED.sendAsTitle();
			match.getWorld().setPVP(true);
			taskEnablePVP = true;
		}

		if (duration <= 0) {
			match.beginMatchEndTransition();
			this.cancel();
		}

		long h = TimeUnit.SECONDS.toHours(duration);
		long m = TimeUnit.SECONDS.toMinutes(duration) - 60 * h;
		long s = duration - 60 * m - 60 * 60 * h;

		ChatColor color;
		if (duration < 5*60) {
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
