package com.bermudalocket.nerdUHC;

import java.util.Map;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.entity.Player;
import com.bermudalocket.nerdUHC.nerdUHC;

public class CombatLogger {
	
	private nerdUHC plugin;
	
	BukkitScheduler scheduler = Bukkit.getScheduler();
	
	private Map<Player, Long> taglist = new HashMap<Player, Long>();
	
	public CombatLogger(nerdUHC plugin) {
		this.plugin = plugin;
	}
	
	public void TagCombat(Player player, Player combatant) {
	
		if ((player == null) || (combatant == null)) {
			return;
		}
		
		if (player == combatant) {
			return;
		}
		
		
		if (!taglist.containsKey(player)) {
			taglist.put(player, new Timestamp(System.currentTimeMillis()).getTime() + 30*100);
		} else {
			taglist.replace(player, new Timestamp(System.currentTimeMillis()).getTime() + 30*100);
		}
		
		if (!taglist.containsKey(combatant)) {
			taglist.put(combatant, new Timestamp(System.currentTimeMillis()).getTime() + 30*100);
		} else {
			taglist.replace(combatant, new Timestamp(System.currentTimeMillis()).getTime() + 30*100);
		}
		
		scheduler.runTaskLater(plugin,  new Runnable() {
			public void run() {
				Iterator<?> i = taglist.entrySet().iterator();
				while (i.hasNext()) {
					Map.Entry pair = (Map.Entry)i.next();
					long timenow = new Timestamp(System.currentTimeMillis()).getTime();
					if ((Long) pair.getValue() <= timenow) {
						i.remove();
					}
				}
			}
		}, 20);
		
	}


}
