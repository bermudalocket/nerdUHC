package com.bermudalocket.nerdUHC;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.modules.UHCDoppel;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;

public class CombatLogger implements Listener {

	private NerdUHC plugin;

	private HashMap<UUID, UHCDoppel> doppellist = new HashMap<UUID, UHCDoppel>();

	public CombatLogger(NerdUHC plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void attemptClearPlayerDrops(PlayerDeathEvent e) {
		if (!plugin.match.isGameStarted())
			return;
		if (plugin.match.playerExists(e.getEntity().getUniqueId())) {
			UHCPlayer p = plugin.match.getPlayer(e.getEntity().getUniqueId());
			if (p.isDoppelDeath()) {
				e.getDrops().clear();
				e.setDroppedExp(0);
			}
		}
	}

	@EventHandler
	public void attemptCombatTag(EntityDamageByEntityEvent e) {
		if (!plugin.match.isGameStarted())
			return;
		if (e.getEntity() == null || e.getDamager() == null)
			return;
		if ((e.getEntity() instanceof Player) || (e.getDamager() instanceof Player)) {
			plugin.combatLogger.tagCombat(e.getEntity(), e.getDamager());
		}

	}

	@EventHandler
	public void attemptSpawnDoppel(PlayerQuitEvent e) {
		if (!plugin.match.isGameStarted())
			return;
		UUID player = e.getPlayer().getUniqueId();
		Location playerlocation = e.getPlayer().getLocation();
		if (plugin.combatLogger.isPlayerTagged(player)) {
			plugin.combatLogger.spawnDoppel(player, playerlocation);
		}
	}

	@EventHandler
	public void attemptDamageDoppel(EntityDamageEvent e) {
		if (!plugin.match.isGameStarted())
			return;
		UUID entity = e.getEntity().getUniqueId();
		if (isDoppel(entity)) {
			getDoppelByEntityUUID(entity).damage(e.getDamage());
		}
	}

	@EventHandler
	public void attemptDoppelDeath(EntityDeathEvent e) {

		if (!plugin.match.isGameStarted())
			return;
		if (e.getEntity() instanceof Player)
			return;

		UUID entity = e.getEntity().getUniqueId();

		if (plugin.combatLogger.isDoppel(entity)) {
			UHCDoppel d = plugin.combatLogger.getDoppelByEntityUUID(entity);

			d.setAlive(false);

			e.getDrops().clear();
			e.setDroppedExp(0);

			if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(d.getPlayer()))) {
				ArrayList<ItemStack> i = d.getDrops();
				i.forEach(item -> e.getDrops().add(item));
				e.setDroppedExp(Math.round(d.getXP()));
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (!plugin.match.isGameStarted()) return;
		UUID player = e.getPlayer().getUniqueId();
		if (plugin.match.playerExists(player)) {
			UHCPlayer p = plugin.match.getPlayer(player);
			if (e.getPlayer().isDead()) return;
			if (!(p.getDoppel() == null)) {
				BukkitRunnable attemptReconcileDoppelTask = new BukkitRunnable() {
					@Override
					public void run() {
						plugin.combatLogger.reconcileDoppelWithPlayer(player);
					}
				};
				attemptReconcileDoppelTask.runTaskLater(plugin, 1);
			}
		}
	}

	public void tagCombat(Entity firstentity, Entity secondentity) {
		Long timenow = System.currentTimeMillis();

		if ((firstentity == null) || (secondentity == null))
			return;
		if (firstentity.equals(secondentity))
			return;

		if (firstentity instanceof Player && (secondentity instanceof Monster || secondentity instanceof Player)) {
			plugin.match.getPlayer(firstentity.getUniqueId()).setCombatTag(timenow);
		}
		if (secondentity instanceof Player && (firstentity instanceof Monster || firstentity instanceof Player)) {
			plugin.match.getPlayer(secondentity.getUniqueId()).setCombatTag(timenow);
		}
	}

	public boolean isDoppel(UUID check) {
		return doppellist.values().stream().anyMatch(doppel -> doppel.getUUID().equals(check)) ? true : false;
	}

	public UHCDoppel getDoppel(UUID player) {
		return doppellist.get(player);
	}

	public UHCDoppel getDoppelByEntityUUID(UUID entity) {
		List<UHCDoppel> search = doppellist.values().stream()
				.filter(doppel -> doppel.getEntity().getUniqueId().equals(entity)).filter(doppel -> doppel.isActive())
				.collect(Collectors.toList());
		if (search.isEmpty()) {
			return null;
		} else {
			return search.get(0);
		}
	}

	public boolean isPlayerTagged(UUID player) {
		Long timenow = System.currentTimeMillis();
		Long playerlasttagged = plugin.match.getPlayer(player).getCombatTag();

		if (playerlasttagged == 0)
			return false;

		Long tagexpires = playerlasttagged + plugin.CONFIG.PLAYER_COMBAT_TAG_TIME * 1000;

		if (timenow < tagexpires) {
			return true;
		} else {
			return false;
		}
	}

	public void spawnDoppel(UUID player, Location location) {
		UHCPlayer p = plugin.match.getPlayer(player);

		if (p.isDead())
			return;

		Entity doppel = plugin.CONFIG.WORLD.spawnEntity(location, plugin.CONFIG.COMBAT_TAG_DOPPEL);
		UHCDoppel d = new UHCDoppel(doppel.getUniqueId(), player, plugin);

		doppellist.put(player, d);
		p.setDoppel(doppel.getUniqueId());
	}

	public void reconcileDoppelWithPlayer(UUID player) {
		if (!doppellist.containsKey(player))
			return;

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
		plugin.CONFIG.WORLD.getLivingEntities().forEach(entity -> {
			if (entity instanceof Monster && !entity.hasAI())
				entity.remove();
		});
		doppellist.clear();
	}

}
