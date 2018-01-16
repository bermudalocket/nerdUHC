package com.bermudalocket.nerdUHC.scoreboards;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.events.MatchStateChangeEvent;
import com.bermudalocket.nerdUHC.events.MatchTimerTickEvent;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;

public class MatchTimer implements Listener {
	
	private NerdUHC plugin;
	private boolean frozen;
	
	public MatchTimer(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMatchStateChangeEvent(MatchStateChangeEvent e) {
		UHCMatchState state = e.getState();
		UHCMatchState laststate = e.getLastState();
		
		if (state.equals(UHCMatchState.DEATHMATCH)) {
			cancel();
		} else if (state.equals(UHCMatchState.INPROGRESS)) {
			run();
		} else if (state.equals(UHCMatchState.END)) {
			if (!isCancelled()) this.cancel();
		} else if (state.equals(UHCMatchState.FROZEN)) {
			freeze();
		} else if (laststate.equals(UHCMatchState.FROZEN)) {
			thaw();
		}
	}
	
	public void run() {
		MatchTimer.runTaskTimer(plugin, 0, 20);
	}
	
	public void cancel() {
		MatchTimer.cancel();
	}
	
	public boolean isCancelled() {
		return MatchTimer.isCancelled();
	}
	
	public boolean frozen() {
		return frozen;
	}
	
	public void freeze() {
		this.frozen = true;
	}
	
	public void thaw() {
		this.frozen = false;
	}
	
	private BukkitRunnable MatchTimer = new BukkitRunnable() {
	
		String timedisplay;
		ChatColor color = ChatColor.WHITE;
		long nexttime;
		
        @Override
        public void run() {
        	
        		plugin.call(new MatchTimerTickEvent());

        		if (frozen()) plugin.match.extendTime(1);
        		
        		nexttime = plugin.match.getTimeEnd() - System.currentTimeMillis();
        		timedisplay = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(nexttime),
        	            TimeUnit.MILLISECONDS.toMinutes(nexttime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(nexttime)),
        	            TimeUnit.MILLISECONDS.toSeconds(nexttime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(nexttime)));
        		
        		if (nexttime <= 600000) color = ChatColor.RED;
        		
        		try {
        			plugin.scoreboardHandler.getObjective("main").setDisplayName(color + "" + ChatColor.BOLD + timedisplay);
        			if (TimeUnit.MILLISECONDS.toSeconds(nexttime) == 10) {
        				plugin.transitionTimer.run();
        			}
        		} catch(Exception e) {
        			// small hiccup, objective just happened to get unregistered
        		}
        }
    };

}
