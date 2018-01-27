package com.bermudalocket.nerdUHC;

import java.util.UUID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.modules.UHCMatch;

public class CombatLogger {

	private NerdUHC plugin;
	private UHCMatch match;

	// doppellist<Player UUID, Entity UUID>
	private HashMap<	UUID, UUID> doppellist = new HashMap<UUID, UUID>();
	
	// tagmap<UUID, Long>
	private HashMap<UUID, Long> tagmap = new HashMap<UUID, Long>();
	
	// deathqueue<UUID>
	private Set<UUID> deathqueue = new HashSet<UUID>();
	
	// nodroplist<UUID>
	private Set<UUID> nodroplist = new HashSet<UUID>();

	public CombatLogger(NerdUHC plugin, UHCMatch match) {
		this.plugin = plugin;
		this.match = match;
	}

	public void combatLog(Player p) {
		tagmap.put(p.getUniqueId(), System.currentTimeMillis());
	}

	public boolean isDoppel(Entity e) {
		return doppellist.values().contains(e.getUniqueId());
	}

	public Entity getDoppel(Player p) {
		UUID d = doppellist.get(p.getUniqueId());
		for (LivingEntity e : plugin.CONFIG.WORLD.getLivingEntities()) {
			if (e.getUniqueId() == d) return e;
		}
		return null;
	}
	
	public Player getPlayerFromDoppel(Entity e) {
		for (Map.Entry<UUID, UUID> entry : doppellist.entrySet()) {
			if (entry.getValue() == e.getUniqueId()) {
				return Bukkit.getPlayer(entry.getKey());
			}
		}
		return null;
	}

	public boolean isPlayerTagged(Player p) {
		if (!tagmap.containsKey(p.getUniqueId())) return false;
		
		Long tagexpires = tagmap.get(p.getUniqueId()) + plugin.CONFIG.PLAYER_COMBAT_TAG_TIME * 1000;
		return System.currentTimeMillis() < tagexpires;
	}
	
	public boolean alreadyDroppedInv(Player p) {
		return nodroplist.contains(p.getUniqueId());
	}
	
	public void removeFromNoDropList(Player p) {
		nodroplist.remove(p.getUniqueId());
	}
	
	public void addToDeathQueue(Player p) {
		deathqueue.add(p.getUniqueId());
		nodroplist.add(p.getUniqueId());
	}
	
	public void spawnDoppel(Player p, Location loc) {
		if (p.isDead() || p.getGameMode() == GameMode.SPECTATOR) return;
		
		Entity doppel = plugin.CONFIG.WORLD.spawnEntity(loc, plugin.CONFIG.COMBAT_TAG_DOPPEL);
		doppellist.put(p.getUniqueId(), doppel.getUniqueId());
	}

	public void reconcileDoppelWithPlayer(Player p) {
		if (!doppellist.containsKey(p.getUniqueId())) return;
		
		if (deathqueue.contains(p.getUniqueId())) {
			p.setHealth(0);
			doppellist.remove(p.getUniqueId());
			deathqueue.remove(p.getUniqueId());
			return;
		}

		LivingEntity d = (LivingEntity) getDoppel(p);
		if (d == null) {
			doppellist.remove(p.getUniqueId());
			return;
		}
		if (d.isDead()) {
			p.setHealth(0);
		} else {
			p.setSaturation(0);
			p.setHealth(d.getHealth());
			d.remove();
		}
		doppellist.remove(p.getUniqueId());
	}

}
