package com.bermudalocket.nerdUHC;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.modules.UHCDoppel;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;

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
		
		if (firstentity instanceof Player && (secondentity instanceof Monster || secondentity instanceof Player)) {
			plugin.match.getPlayer(first).setCombatTag(timenow);
		}
		if (secondentity instanceof Player && (firstentity instanceof Monster || firstentity instanceof Player)) {
			plugin.match.getPlayer(second).setCombatTag(timenow);
		}
	}

	public boolean isDoppel(UUID check) {
		return doppellist.values().stream().
						anyMatch(doppel -> doppel.getUUID().equals(check))
						? true : false;
	}
	
	public UHCDoppel getDoppel(UUID player) {
		return doppellist.get(player);
	}
	
	public UHCDoppel getDoppelByEntityUUID(UUID entity) {
		List<UHCDoppel> search = doppellist.values().stream().
									filter(doppel -> doppel.getEntity().getUniqueId().equals(entity)).
									filter(doppel -> doppel.isActive()).
									collect(Collectors.toList());
		if (search.isEmpty()) {
			return null;
		} else {
			return search.get(0);
		}
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
		UHCPlayer p = plugin.match.getPlayer(player);

		if (p.isDead()) return;
		
		Entity doppel = plugin.match.getWorld().spawnEntity(location, plugin.CONFIG.COMBAT_TAG_DOPPEL);
		UHCDoppel d = new UHCDoppel(doppel.getUniqueId(), player, plugin);
		
		doppellist.put(player, d);
		p.setDoppel(doppel.getUniqueId());
	}
	
	public void reconcileDoppelWithPlayer(UUID player) {
		if (!doppellist.containsKey(player)) return;
		
		UHCPlayer p = plugin.match.getPlayer(player);
		UHCDoppel d = doppellist.get(player);

		if (d.isDead()) {
			p.setDoppelDeath(true);
			p.kill();
		} else {
			p.bukkitPlayer().setSaturation(0);
			p.bukkitPlayer().setHealth(d.getHealth());
			d.damage(200.0);
		}

		plugin.match.getPlayer(player).setDoppel(null);
		doppellist.remove(player);
	}
	
	public void clearDoppels() {
		plugin.match.getWorld().getLivingEntities().forEach(entity -> {
			if (entity instanceof Monster && !entity.hasAI()) entity.remove();
		});
		doppellist.clear();
	}
	
}
