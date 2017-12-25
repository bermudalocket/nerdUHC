package com.bermudalocket.nerdUHC;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;

public class CombatLogger {
	
	private NerdUHC plugin;
	private Map<UUID, Long> taglist = new HashMap<UUID, Long>();
	
	public CombatLogger(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	public void tagCombat(Player player, Player combatant) {
		
		Long timenow = System.currentTimeMillis();
	
		if ((player == null) || (combatant == null)) return;
		if (player == combatant) return;
		
		taglist.put(player.getUniqueId(), timenow);
		taglist.put(combatant.getUniqueId(), timenow);
		
	}

}
