package com.bermudalocket.nerdUHC.listeners;

import java.util.List;
import java.util.UUID;

import com.bermudalocket.nerdUHC.Configuration;
import com.bermudalocket.nerdUHC.NerdUHC;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.bermudalocket.nerdUHC.util.MatchState;
import com.bermudalocket.nerdUHC.util.UHCSounds;

import static com.bermudalocket.nerdUHC.NerdUHC.COMBAT_LOGGER;
import static com.bermudalocket.nerdUHC.NerdUHC.MATCH_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.SCOREBOARD_HANDLER;

public class MatchListener implements Listener {

	public MatchListener() {
		NerdUHC.PLUGIN.getServer().getPluginManager().registerEvents(this, NerdUHC.PLUGIN);
	}

	public void stopListening() {
		HandlerList.unregisterAll(this);
	}

	// ----------------------------------------------------------------

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent e) {
		if (MATCH_HANDLER.getMatch().inState(MatchState.PREGAME)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Location spawnLoc = e.getFrom().getWorld().getSpawnLocation();
		if (MATCH_HANDLER.getMatch().inState(MatchState.PREGAME)
				&& e.getTo().distanceSquared(spawnLoc) > Configuration.SPAWN_BARRIER_RADIUS_SQUARED) {
			e.getPlayer().teleport(e.getFrom());
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() == null || e.getDamager() == null || e.getEntity() == e.getDamager()) {
			return;
		}
		if (e.getEntity() instanceof Player) { 
			COMBAT_LOGGER.combatLog((Player) e.getEntity());
		}
		if (e.getDamager() instanceof Player) { 
			COMBAT_LOGGER.combatLog((Player) e.getDamager());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (MATCH_HANDLER.getMatch().inState(MatchState.PREGAME)) {
			return;
		}
		if (COMBAT_LOGGER.isPlayerTagged(p)) {
			COMBAT_LOGGER.spawnDoppel(p);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		SCOREBOARD_HANDLER.removePlayerFromTeam(p, true);
		UHCSounds.PLAYERDEATH.playSound();
		MATCH_HANDLER.getMatch().checkForMatchEnd();
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		Entity entity = e.getEntity();
		
		if (entity instanceof Player) return;
		if (COMBAT_LOGGER.isDoppel(entity)) {
			UUID playerUUID = COMBAT_LOGGER.getPlayerFromDoppel(entity);
			if (playerUUID == null) return;

			COMBAT_LOGGER.addToDeathQueue(playerUUID);

			e.getDrops().clear();
			e.setDroppedExp(COMBAT_LOGGER.getExp(playerUUID));
			
			for (ItemStack i : COMBAT_LOGGER.getInventory(playerUUID).getContents()) {
				e.getDrops().add(i);
			}
			for (ItemStack i : COMBAT_LOGGER.getInventory(playerUUID).getArmorContents()) {
				e.getDrops().add(i);
			}
		} else {
			removeGhastTears(e);
		}
	}

	private void removeGhastTears(EntityDeathEvent e) {
		List<ItemStack> drops = e.getDrops();
		for (int i = 0; i < drops.size(); i++) {
			if (drops.get(i).getType() == Material.GHAST_TEAR) {
				drops.remove(i);
				drops.add(new ItemStack(Material.GOLD_INGOT, 1));
			}
		}
		e.getDrops().clear();
		e.getDrops().addAll(drops);
	}

}
