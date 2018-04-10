package com.bermudalocket.nerdUHC.match;

import java.util.UUID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.PlayerInventory;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCLibrary;

public class CombatLogger implements java.io.Serializable {

	private final HashMap<UUID, UUID> _playerUuidToDoppelUuidMap = new HashMap<>();

	private final HashMap<UUID, Long> _playerUuidToTagTimeMap = new HashMap<>();

	private final Set<UUID> _deathQueueSet = new HashSet<>();

	private final HashMap<UUID, PlayerInventory> _playerUuidToInventoryMap = new HashMap<>();

	private final HashMap<UUID, Integer> _playerUuidToExpMap = new HashMap<>();

	// ----------------------------------------------------------------

	public void combatLog(Player p) {
		_playerUuidToTagTimeMap.put(p.getUniqueId(), System.currentTimeMillis());
	}
	
	private void logInventory(Player p) {
		_playerUuidToInventoryMap.put(p.getUniqueId(), p.getInventory());
		_playerUuidToExpMap.put(p.getUniqueId(), (int) p.getExp());
	}
	
	public PlayerInventory getInventory(UUID uuid) {
		return _playerUuidToInventoryMap.get(uuid);
	}
	
	public Integer getExp(UUID uuid) {
		return _playerUuidToExpMap.get(uuid);
	}

	// ----------------------------------------------------------------

	public boolean isDoppel(Entity e) {
		return _playerUuidToDoppelUuidMap.values().contains(e.getUniqueId());
	}

	private LivingEntity getDoppel(Player p) {
		UUID doppelUuid = _playerUuidToDoppelUuidMap.get(p.getUniqueId());
		World world = NerdUHC.MATCH_HANDLER.getMatch().getWorld();
		for (LivingEntity e : world.getLivingEntities()) {
			if (e.getUniqueId().equals(doppelUuid)) return e;
		}
		return null;
	}
	
	public UUID getPlayerFromDoppel(Entity e) {
		for (Map.Entry<UUID, UUID> entry : _playerUuidToDoppelUuidMap.entrySet()) {
			if (entry.getValue() == e.getUniqueId()) {
				return entry.getKey();
			}
		}
		return null;
	}

	public boolean isPlayerTagged(Player p) {
		if (!_playerUuidToTagTimeMap.containsKey(p.getUniqueId())) return false;
		
		Long tagexpires = _playerUuidToTagTimeMap.get(p.getUniqueId()) + NerdUHC.CONFIG.PLAYER_COMBAT_TAG_TIME * 1000;
		return System.currentTimeMillis() < tagexpires;
	}

	private void callDeathEvent(Player p) {
		Bukkit.getPluginManager().callEvent(new PlayerDeathEvent(p, null, 0, p.getDisplayName() + " thought they could get away with combat logging!"));
	}

	public void addToDeathQueue(UUID playeruuid) {
		_deathQueueSet.add(playeruuid);
	}
	
	public void spawnDoppel(Player p) {
		if (p.isDead() || p.getGameMode() == GameMode.SPECTATOR) return;
		World world = NerdUHC.MATCH_HANDLER.getMatch().getWorld();

		LivingEntity doppel = configuredDoppel(p);
		for (Entity e : world.getNearbyEntities(p.getLocation(), 8, 8, 8)) {
			if (e instanceof Monster) ((Monster) e).setTarget(doppel);
		}
		_playerUuidToDoppelUuidMap.put(p.getUniqueId(), doppel.getUniqueId());
		logInventory(p);
	}
	
	private LivingEntity configuredDoppel(Player p) {
		World world = NerdUHC.MATCH_HANDLER.getMatch().getWorld();
		LivingEntity doppel = (LivingEntity) world.spawnEntity(p.getLocation(), NerdUHC.CONFIG.COMBAT_TAG_DOPPEL);
		doppel.setAI(false);
		doppel.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
		doppel.setHealth(p.getHealth());
		doppel.setCustomName(p.getDisplayName());
		doppel.setGlowing(true);
		return doppel;
	}

	public void reconcileDoppelWithPlayer(Player p) {
		if (!_playerUuidToDoppelUuidMap.containsKey(p.getUniqueId())) return;
		if (_deathQueueSet.contains(p.getUniqueId())) {
			callDeathEvent(p);
			_playerUuidToDoppelUuidMap.remove(p.getUniqueId());
			_deathQueueSet.remove(p.getUniqueId());
			UHCLibrary.LIB_CL_DOPPELDEAD.tell(p);
			return;
		}
		LivingEntity doppel = getDoppel(p);
		if (doppel == null) {
			_playerUuidToDoppelUuidMap.remove(p.getUniqueId());
			return;
		}
		if (doppel.isDead()) {
			UHCLibrary.LIB_CL_DOPPELDEAD.tell(p);
			callDeathEvent(p);
		} else {
			if (p.getHealth() == doppel.getHealth()) {
				UHCLibrary.LIB_CL_NODMG.tell(p);
			} else {
				UHCLibrary.LIB_CL_DMG.rep(p, "%d", "" + (p.getHealth() - doppel.getHealth()));
				p.setSaturation(0);
				p.setHealth(doppel.getHealth());
			}
			doppel.remove();
		}
		_playerUuidToDoppelUuidMap.remove(p.getUniqueId());
	}

}
