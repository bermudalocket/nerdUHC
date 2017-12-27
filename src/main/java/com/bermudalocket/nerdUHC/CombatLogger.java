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
		
		plugin.getLogger().info("Tagged " + player.getName() + " and " + combatant.getName() + " at " + timenow);
		
	}
	
	public boolean isPlayerTagged(Player player) {
		
		Long timenow = System.currentTimeMillis();
		Long playerlasttagged = taglist.get(player.getUniqueId());
		
		if (playerlasttagged != null) {
			if (playerlasttagged + plugin.CONFIG.PLAYER_COMBAT_TAG_TIME*100 <= timenow) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
		
	}

}
