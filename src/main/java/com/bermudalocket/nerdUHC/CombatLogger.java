package com.bermudalocket.nerdUHC;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.events.DoppelSpawnEvent;
import com.bermudalocket.nerdUHC.modules.UHCDoppel;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;

public class CombatLogger implements Listener {

	private NerdUHC plugin;

	private HashMap<UHCPlayer, UHCDoppel> doppellist = new HashMap<UHCPlayer, UHCDoppel>();

	public CombatLogger(NerdUHC plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDoppelSpawn(DoppelSpawnEvent e) {
		e.getPlayer().setDoppel(e.getDoppel());
		doppellist.put(e.getPlayer(), e.getDoppel());
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (!plugin.match.isGameStarted())
			return;
		if (e.getEntity() == null || e.getDamager() == null)
			return;
		if (e.getEntity() instanceof Player || e.getDamager() instanceof Player) {
			combatLog(e.getEntity(), e.getDamager());
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!plugin.match.isGameStarted())
			return;
		if (plugin.match.playerExists(e.getEntity().getUniqueId())) {
			UHCPlayer p = plugin.match.getPlayer(e.getEntity().getUniqueId());
			if (!p.dropInventory()) {
				e.getDrops().clear();
				e.setDroppedExp(0);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (!plugin.match.isGameStarted())
			return;
		if (!plugin.match.playerExists(e.getPlayer().getUniqueId()))
			return;

		UHCPlayer p = plugin.match.getPlayer(e.getPlayer().getUniqueId());
		if (plugin.combatLogger.isPlayerTagged(p)) {
			plugin.combatLogger.spawnDoppel(p, p.bukkitPlayer().getLocation());
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (!plugin.match.isGameStarted())
			return;
		if (e.getEntity() instanceof Player)
			return;
		if (isDoppel(e.getEntity())) {
			UHCDoppel d = getDoppelByEntityUUID(e.getEntity().getUniqueId());
			e.getDrops().clear();
			e.setDroppedExp(0);
			d.getPlayer().setInvDrop(false);
			if (!Bukkit.getOnlinePlayers().contains(d.getPlayer().bukkitPlayer())) {
				ArrayList<ItemStack> i = d.getDrops();
				i.forEach(item -> e.getDrops().add(item));
				e.setDroppedExp(Math.round(d.getXP()));
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (!plugin.match.isGameStarted())
			return;
		if (!plugin.match.playerExists(e.getPlayer().getUniqueId()))
			return;
		UHCPlayer p = plugin.match.getPlayer(e.getPlayer().getUniqueId());
		if (!p.equals(null)) {
			if (p.isDead())
				return;
			if (!p.getDoppel().equals(null)) {
				BukkitRunnable attemptReconcileDoppelTask = new BukkitRunnable() {
					@Override
					public void run() {
						reconcileDoppelWithPlayer(p);
					}
				};
				attemptReconcileDoppelTask.runTaskLater(plugin, 1);
			}
		}
	}

	public void combatLog(Entity a, Entity b) {
		if (a == null || b == null || a == b)
			return;
		Long timenow = System.currentTimeMillis();
		if (a instanceof Player && (b instanceof Monster || b instanceof Player)) {
			plugin.match.getPlayer(a.getUniqueId()).setCombatTag(timenow);
		}
		if (b instanceof Player && (a instanceof Monster || a instanceof Player)) {
			plugin.match.getPlayer(b.getUniqueId()).setCombatTag(timenow);
		}
	}

	public boolean isDoppel(Entity e) {
		for (UHCDoppel d : doppellist.values()) {
			if (d.getUUID() == e.getUniqueId())
				return true;
		}
		return false;
	}

	public UHCDoppel getDoppel(UHCPlayer p) {
		return p.getDoppel();
	}

	public UHCDoppel getDoppelByEntityUUID(UUID entity) {
		List<UHCDoppel> search = doppellist.values().stream()
				.filter(doppel -> doppel.bukkitEntity().getUniqueId() == entity).collect(Collectors.toList());
		if (search.isEmpty()) {
			return null;
		} else {
			return search.get(0);
		}
	}

	public boolean isPlayerTagged(UHCPlayer p) {
		if (p.getCombatTag() <= 0)
			return false;
		Long tagexpires = p.getCombatTag() + plugin.CONFIG.PLAYER_COMBAT_TAG_TIME * 1000;
		return System.currentTimeMillis() < tagexpires;
	}

	public void spawnDoppel(UHCPlayer p, Location loc) {
		if (p.isDead())
			return;
		LivingEntity doppel = (LivingEntity) plugin.CONFIG.WORLD.spawnEntity(loc, plugin.CONFIG.COMBAT_TAG_DOPPEL);
		UHCDoppel d = new UHCDoppel(doppel, p, plugin);
		plugin.call(new DoppelSpawnEvent(p, d));
	}

	public void reconcileDoppelWithPlayer(UHCPlayer p) {
		if (!doppellist.containsKey(p))
			return;

		UHCDoppel d = p.getDoppel();
		if (d.bukkitEntity().isDead()) {
			p.bukkitPlayer().damage(200.0);
		} else {
			p.bukkitPlayer().setSaturation(0);
			p.bukkitPlayer().setHealth(d.bukkitEntity().getHealth());
			d.bukkitEntity().damage(200.0);
		}
		p.setDoppel(null);
		doppellist.remove(p);
	}

	public void clearDoppels() {
		for (UHCDoppel d : doppellist.values()) {
			d.bukkitEntity().remove();
		}
		doppellist.clear();
	}

}
