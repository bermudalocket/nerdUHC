package com.bermudalocket.nerdUHC;

import java.util.UUID;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCDoppel;

public class CombatLogger {
	
	private NerdUHC plugin;
	
	private HashMap<UUID, UHCDoppel> doppellist = new HashMap<UUID, UHCDoppel>();
	
	public CombatLogger(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	public void tagCombat(UUID first, UUID second) {
		Long timenow = System.currentTimeMillis();
		Entity firstentity = Bukkit.getEntity(first);
		Entity secondentity = Bukkit.getEntity(second);

		if ((first == null) || (second == null)) return;
		if (first.equals(second)) return;
		
		if (firstentity instanceof Player) plugin.match.getPlayer(first).setCombatTag(timenow);
		if (secondentity instanceof Player) plugin.match.getPlayer(second).setCombatTag(timenow);
	}

	public boolean isDoppel(UUID check) {
		return doppellist.values().stream().
						anyMatch(doppel -> doppel.getEntity().getUniqueId().equals(check))
						? true : false;
	}
	
	public UHCDoppel getDoppel(UUID doppel) {
		return doppellist.get(doppel);
	}
	
	public boolean isPlayerTagged(UUID player) {
		Long timenow = System.currentTimeMillis();
		Long playerlasttagged = plugin.match.getPlayer(player).getCombatTag();
		
		if (playerlasttagged == 0) return false;
		
		Long tagexpires = playerlasttagged + plugin.CONFIG.PLAYER_COMBAT_TAG_TIME*1000;
		
		if (timenow < tagexpires) {
			return true;
		} else {
			return false;
		}
	}
	
	public void spawnDoppel(UUID player, Location location) {
		Player p = Bukkit.getPlayer(player);

		if (p.isDead()) return;
		
		Entity doppel = plugin.match.getWorld().spawnEntity(location, plugin.CONFIG.COMBAT_TAG_DOPPEL);
		UHCDoppel d = new UHCDoppel(doppel.getUniqueId(), player);
		
		doppellist.put(player, d);
		
		d.setDrops(p.getInventory().getContents());
		d.setMaxHealth(20);
		d.setHealth(p.getHealth());
		
	}
	
	public void reconcileDoppelWithPlayer(UUID player) {
		if (!doppellist.containsKey(player)) return;
		
		Player p = Bukkit.getPlayer(player);
		UHCDoppel d = doppellist.get(player);
		
		if (d.isDead()) {
			p.damage(200.0);
		} else {
			p.setSaturation(0);
			p.setHealth(d.getHealth());
			d.despawn();
		}
		
		doppellist.remove(player);
		
	}
	
}
