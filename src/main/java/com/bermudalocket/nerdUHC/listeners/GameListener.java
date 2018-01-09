package com.bermudalocket.nerdUHC.listeners;

import java.util.UUID;

import org.bukkit.Location;
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
			
			p.unite();
			
			if (p.isDead()) return;
			
			if (p.getDoppel() != null) {

				BukkitRunnable task = new BukkitRunnable() {
					@Override
					public void run() {
						plugin.combatLogger.reconcileDoppelWithPlayer(player);
					}
				};
				task.runTaskLater(plugin, 1);
				
			}
			
		} else {
			plugin.match.registerPlayer(player, "SPECTATOR");
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
		
		p.setAlive(false);
		
	}
	
	@EventHandler 
	public void onEntityDamage(EntityDamageEvent e) {
		
		if (!plugin.match.isGameStarted()) return;
		
		UUID entity = e.getEntity().getUniqueId();
		
		if (plugin.combatLogger.isDoppel(entity)) {
			plugin.combatLogger.getDoppel(entity).damage(e.getDamage());
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
		
		UUID entity = e.getEntity().getUniqueId();
		
		if (plugin.combatLogger.isDoppel(entity)) {
			UHCDoppel d = plugin.combatLogger.getDoppel(entity);
			ItemStack[] i = d.getDrops();
			
			d.setAlive(false);
			d.recordDeath(e);
			
			e.getDrops().clear();
			for (int j = 0; j <= i.length; j++) {
				e.getDrops().add(i[j]);
			}
		}
	}

}
