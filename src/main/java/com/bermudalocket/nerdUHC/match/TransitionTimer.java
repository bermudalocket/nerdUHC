package com.bermudalocket.nerdUHC.match;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.events.MatchStateChangeEvent;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCSound;

public class TransitionTimer extends BukkitRunnable {
	
	private NerdUHC plugin;
	
	public TransitionTimer(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	public void run() {
		TransitionTimer.runTaskTimer(plugin, 0, 20);
	}
	
	BukkitRunnable TransitionTimer = new BukkitRunnable() {
		int countfrom = 10;
		ChatColor color = ChatColor.WHITE;
	
        @Override
        public void run() {
        		if (countfrom == 0) {
        			plugin.CONFIG.WORLD.setPVP(false);
        			plugin.match.freezePlayers(true);
        		    UHCSound.MATCHEND.playSound();
        		    if (plugin.CONFIG.DO_DEATHMATCH && plugin.match.numberOfAlivePlayers() > 1) {
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
