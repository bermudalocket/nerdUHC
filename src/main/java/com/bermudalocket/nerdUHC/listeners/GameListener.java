package com.bermudalocket.nerdUHC.listeners;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCDoppel;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;

public class GameListener implements Listener {
	
	private NerdUHC plugin;
	
	public GameListener(NerdUHC plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
		if (!plugin.match.isGameStarted()) return;
		
		UUID player = e.getPlayer().getUniqueId();
		
		if (plugin.match.playerExists(player)) {
			UHCPlayer p = plugin.match.getPlayer(player);
			
			if (e.getPlayer().isDead()) return;

			if (!(p.getDoppel() == null)) {
					BukkitRunnable task = new BukkitRunnable() {
						@Override
						public void run() {
							plugin.combatLogger.reconcileDoppelWithPlayer(player);
						}
					};
					task.runTaskLater(plugin, 1);
			}
		} else {
			plugin.match.registerPlayer(player, plugin.match.getSpectatorTeam());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		
		if (!plugin.match.isGameStarted()) return;
		
		UUID player = e.getPlayer().getUniqueId();
		Location playerlocation = e.getPlayer().getLocation();
		
		if (plugin.combatLogger.isPlayerTagged(player)) {
			plugin.combatLogger.spawnDoppel(player, playerlocation);
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!plugin.match.isGameStarted()) return;
		
		UHCPlayer p = plugin.match.getPlayer(e.getEntity().getUniqueId());
		if (p.isDoppelDeath()) {
			e.getDrops().clear();
			e.setDroppedExp(0);
		}
		
		// set to spectator but preserve team
		p.bukkitPlayer().setGameMode(GameMode.SPECTATOR);
		p.setAlive(false);
	}
	
	@EventHandler 
	public void onEntityDamage(EntityDamageEvent e) {
		
		if (!plugin.match.isGameStarted()) return;
		
		UUID entity = e.getEntity().getUniqueId();
		
		if (plugin.combatLogger.isDoppel(entity)) {
			plugin.combatLogger.getDoppelByEntityUUID(entity).damage(e.getDamage());
		}
	}
	
	@EventHandler
	public void attemptCombatTag(EntityDamageByEntityEvent e) {
		
		if (!plugin.match.isGameStarted()) return;
		
		if (e.getEntity() == null || e.getDamager() == null) return;
		
		if ((e.getEntity() instanceof Player) || (e.getDamager() instanceof Player)) {
			plugin.combatLogger.tagCombat(e.getEntity().getUniqueId(), e.getDamager().getUniqueId());
		}

	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		
		if (!plugin.match.isGameStarted()) return;
		if (e.getEntity() instanceof Player) return;
		
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
	public void onPlayerMove(PlayerMoveEvent e) {
		if (plugin.match.arePlayersFrozen() && plugin.match.playerExists(e.getPlayer().getUniqueId())) {
			if (!e.getFrom().toVector().equals(e.getTo().toVector())) {
				e.getTo().setDirection(e.getFrom().toVector());
			}
		}
	}

}
