package com.bermudalocket.nerdUHC.match;

import java.util.UUID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.PlayerInventory;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCLibrary;

public class CombatLogger {

	private final NerdUHC plugin;

	// doppellist<Player UUID, Entity UUID>
	private final HashMap<UUID, UUID> doppellist = new HashMap<>();
	
	// tagmap<Player UUID, Long>
	private final HashMap<UUID, Long> tagmap = new HashMap<>();
	
	private final Set<UUID> deathqueue = new HashSet<>();
	private final Set<UUID> nodroplist = new HashSet<>();
	private final HashMap<UUID, PlayerInventory> invlist = new HashMap<>();
	private final HashMap<UUID, Integer> explist = new HashMap<>();

	public CombatLogger(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	// LOG METHOD

	public void combatLog(Player p) {
		tagmap.put(p.getUniqueId(), System.currentTimeMillis());
	}
	
	// DROPS and EXP
	
	private void logInventory(Player p) {
		invlist.put(p.getUniqueId(), p.getInventory());
		explist.put(p.getUniqueId(), (int) p.getExp());
	}
	
	public PlayerInventory getInventory(UUID uuid) {
		return invlist.get(uuid);
	}
	
	public Integer getExp(UUID uuid) {
		return explist.get(uuid);
	}
	
	// DOPPEL METHODS

	public boolean isDoppel(Entity e) {
		return doppellist.values().contains(e.getUniqueId());
	}

	private LivingEntity getDoppel(Player p) {
		UUID d = doppellist.get(p.getUniqueId());
		for (LivingEntity e : plugin.CONFIG.WORLD.getLivingEntities()) {
			if (e.getUniqueId().equals(d)) return e;
		}
		return null;
	}
	
	public UUID getPlayerFromDoppel(Entity e) {
		for (Map.Entry<UUID, UUID> entry : doppellist.entrySet()) {
			if (entry.getValue() == e.getUniqueId()) {
				return entry.getKey();
			}
		}
		return null;
	}

	public boolean isPlayerTagged(Player p) {
		if (!tagmap.containsKey(p.getUniqueId())) return false;
		
		Long tagexpires = tagmap.get(p.getUniqueId()) + plugin.CONFIG.PLAYER_COMBAT_TAG_TIME * 1000;
		return System.currentTimeMillis() < tagexpires;
	}

	@SuppressWarnings("deprecation")
	private void callDeathEvent(Player p) {
		p.setLastDamageCause(new EntityDamageEvent(p, EntityDamageEvent.DamageCause.CUSTOM, 0));
		Bukkit.getPluginManager().callEvent(new PlayerDeathEvent(p, null, 0, p.getDisplayName() + " thought they could get away with combat logging!"));
	}
	
	public boolean alreadyDroppedInv(Player p) {
		return nodroplist.contains(p.getUniqueId());
	}

	public void addToDeathQueue(UUID playeruuid) {
		deathqueue.add(playeruuid);
		nodroplist.add(playeruuid);
	}
	
	public void spawnDoppel(Player p) {
		if (p.isDead() || p.getGameMode() == GameMode.SPECTATOR) {
			return;
		}
		
		LivingEntity doppel = configuredDoppel(p);
		for (Entity e : plugin.CONFIG.WORLD.getNearbyEntities(p.getLocation(), 8, 8, 8)) {
			if (e instanceof Monster) {
				((Monster) e).setTarget(doppel);
			}
		}
		doppellist.put(p.getUniqueId(), doppel.getUniqueId());
		logInventory(p);
	}
	
	private LivingEntity configuredDoppel(Player p) {
		LivingEntity doppel = (LivingEntity) plugin.CONFIG.WORLD.spawnEntity(p.getLocation(), plugin.CONFIG.COMBAT_TAG_DOPPEL);
		doppel.setAI(false);
		doppel.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
		doppel.setHealth(p.getHealth());
		doppel.setCustomName(p.getDisplayName());
		doppel.setGlowing(true);
		return doppel;
	}

	public void reconcileDoppelWithPlayer(Player p) {
		
		if (!doppellist.containsKey(p.getUniqueId())) return;
		
		if (deathqueue.contains(p.getUniqueId())) {
			callDeathEvent(p);
			doppellist.remove(p.getUniqueId());
			deathqueue.remove(p.getUniqueId());
			UHCLibrary.LIB_CL_DOPPELDEAD.emph(p);
			return;
		}

		LivingEntity doppel = getDoppel(p);
		if (doppel == null) {
			doppellist.remove(p.getUniqueId());
			return;
		}
		if (doppel.isDead()) {
			UHCLibrary.LIB_CL_DOPPELDEAD.emph(p);
			callDeathEvent(p);
		} else {
			if (p.getHealth() == doppel.getHealth()) {
				UHCLibrary.LIB_CL_NODMG.emph(p);
			} else {
				UHCLibrary.LIB_CL_DMG.emph(p, "%d", "" + (p.getHealth() - doppel.getHealth()));
				p.setSaturation(0);
				p.setHealth(doppel.getHealth());
			}
			doppel.remove();
		}
		doppellist.remove(p.getUniqueId());
	}

}
