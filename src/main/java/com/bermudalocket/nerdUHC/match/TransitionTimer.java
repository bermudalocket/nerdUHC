package com.bermudalocket.nerdUHC.match;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.events.MatchStateChangeEvent;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;

public class TransitionTimer {
	
	private NerdUHC plugin;
	
	public TransitionTimer(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	public void run() {
		TransitionTimer.runTaskTimer(plugin, 0, 20);
	}
	
	public void cancel() {
		TransitionTimer.cancel();
	}
	
	public boolean isCancelled() {
		return TransitionTimer.isCancelled();
	}
	
	BukkitRunnable TransitionTimer = new BukkitRunnable() {
		int countfrom = 10;
		ChatColor color = ChatColor.WHITE;
	
        @Override
        public void run() {
        		if (countfrom == 0) {
        			//plugin.match.setPVP(false);
        			plugin.match.freezePlayers(true);
        		    Bukkit.getOnlinePlayers().forEach(player -> 
		    				player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 10, 1));
        		    if (plugin.CONFIG.DO_DEATHMATCH) {
        		    		plugin.call(new MatchStateChangeEvent(UHCMatchState.DEATHMATCH));
        		    } else {
        		    		plugin.call(new MatchStateChangeEvent(UHCMatchState.END));
        		    }
        			this.cancel();
        		} else {
        			if (countfrom <= 3) color = ChatColor.RED;
        			Bukkit.getOnlinePlayers().forEach(player -> 
        				player.sendTitle(color + "" + countfrom, null, 10, 0, 10));
        		}
        		countfrom--;
        }
    };

}
