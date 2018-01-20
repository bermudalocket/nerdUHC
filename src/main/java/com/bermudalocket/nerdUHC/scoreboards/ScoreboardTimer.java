package com.bermudalocket.nerdUHC.scoreboards;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;

public class ScoreboardTimer  {

	private NerdUHC plugin;
	private boolean cancelled;

	public ScoreboardTimer(NerdUHC plugin) {
		this.plugin = plugin;
	}

	public void run() {
		this.cancelled = false;
		MatchTimer.runTaskTimer(plugin, 0, 20);
	}

	public void cancel() {
		try {
			this.cancelled = true;
		} catch (Exception f) {
		}
	}

	public boolean isCancelled() {
		return MatchTimer.isCancelled();
	}

	private BukkitRunnable MatchTimer = new BukkitRunnable() {

		String timedisplay;
		ChatColor color = ChatColor.WHITE;
		long nexttime;
		long sec;

		@Override
		public void run() {
			
			if (cancelled || plugin.match.getMatchState() == UHCMatchState.PREGAME) return;

			if (plugin.match.arePlayersFrozen())
				plugin.match.extendTime(1);

			nexttime = plugin.match.getTimeEnd() - System.currentTimeMillis();
			timedisplay = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(nexttime),
					TimeUnit.MILLISECONDS.toMinutes(nexttime)
							- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(nexttime)),
					TimeUnit.MILLISECONDS.toSeconds(nexttime)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(nexttime)));

			if (nexttime <= 600000)
				color = ChatColor.RED;

			try {
				sec = TimeUnit.MILLISECONDS.toSeconds(nexttime);
				plugin.scoreboardHandler.getObjective("main").setDisplayName(color + "" + ChatColor.BOLD + timedisplay);
				if (sec == 10) {
					plugin.match.transitionTimer.run();
				} else if (sec == 0 || sec < 0) {
					this.cancel();
				}
			} catch (Exception e) {
				// small hiccup, objective just happened to get unregistered
			}
		}
	};

}
