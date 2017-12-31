package com.bermudalocket.nerdUHC;

import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bermudalocket.nerdUHC.NerdUHC;

/////////////////////////////////////////////////////////////////////////////
//
//	CombatLogger
//
//

public class CombatLogger {
	
	private Map<UUID, Long> taglist = new HashMap<UUID, Long>();		// player UUID
	private Map<UUID, UUID> doppellist = new HashMap<UUID, UUID>();		// player UUID to entity UUID
	private Map<UUID, ArrayList<ItemStack>> doppeldrops = new HashMap<UUID, ArrayList<ItemStack>>();	// player UUID
	private Map<UUID, UUID> lastattackerlist = new HashMap<UUID, UUID>();
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	ListenForEntityDamageByEntityEvent
	//
	//

	public void tagCombat(UUID player, UUID combatant) {
		
		Long timenow = System.currentTimeMillis();
		Entity combatantentity = Bukkit.getEntity(combatant);
	
		if ((player == null) || (combatant == null)) return;
		if (player == combatant) return;
		
		taglist.put(player, timenow);
		if (combatantentity instanceof Player) { 
			taglist.put(combatant, timenow);
		} else if (combatantentity instanceof Creature) {
			lastattackerlist.put(player, combatant);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	ListenForEntityDeathEvent
	//	
	//
	
	public boolean isDoppel(UUID doppel) {
		return doppellist.containsValue(doppel);
	}
	
	public ArrayList<ItemStack> getDoppelDrops(UUID player) {
		return doppeldrops.get(player);
	}
	
	public UUID getDoppelPlayer(UUID doppel) {
		for (UUID player : doppellist.keySet()) {
			if (doppellist.get(player).equals(doppel)) {
				return player;
			}
		}
		return null;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	ListenForPlayerQuitEvent
	//	
	//
	
	public boolean isPlayerTagged(UUID player) {
		
		Long timenow = System.currentTimeMillis();
		Long playerlasttagged = taglist.get(player);		
		Long tagexpires = playerlasttagged + NerdUHC.CONFIG.PLAYER_COMBAT_TAG_TIME*100;
		
		if (timenow < tagexpires) {
			return true;
		} else {
			return false;
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void spawnDoppel(UUID playeruuid, Location location) {
		
		Player player = Bukkit.getPlayer(playeruuid);
		
		// Spawn doppel
		Entity combatdoppel = NerdUHC.getWorld().spawnEntity(player.getLocation(), NerdUHC.CONFIG.COMBAT_TAG_DOPPEL);
		doppellist.put(player.getUniqueId(), combatdoppel.getUniqueId());

		// Set doppel properties
		((Damageable) combatdoppel).setMaxHealth(20);
		((Damageable) combatdoppel).setHealth(player.getHealth());
		((LivingEntity) combatdoppel).setAI(false);
		combatdoppel.setCustomName(player.getName());
		combatdoppel.setCustomNameVisible(true);
		combatdoppel.setFireTicks(20*20);
		
		// Make doppel drop player's inventory
		ArrayList<ItemStack> inventory = new ArrayList<>();
		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null) inventory.add(item);
		}
		for (ItemStack item : player.getInventory().getArmorContents()) {
			if (item != null) inventory.add(item);
		}
		doppeldrops.put(player.getUniqueId(), inventory);
		
		// If player was being attacked when they logged out, make the attacker target doppel
		Creature lastattacker = (Creature) Bukkit.getEntity(lastattackerlist.get(playeruuid));
		if (lastattacker != null) {
			if (!lastattacker.isDead()) {
				lastattacker.setTarget((LivingEntity) combatdoppel);
			}
		}

	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	ListenForPlayerJoinEvent
	//	
	//
	
	public boolean doesPlayerHaveDoppel(UUID player) {
		
		return doppellist.containsKey(player);
	}
	
	public void reconcileDoppelWithPlayer(UUID playeruuid) {

		Player player = Bukkit.getPlayer(playeruuid);
		UUID combatdoppelID = doppellist.get(player.getUniqueId());
		Damageable combatdoppel = ((Damageable) Bukkit.getEntity(combatdoppelID));
		
		if (combatdoppel.isDead()) {
			player.damage(20);
			player.sendMessage(ChatColor.RED + "Whoops - you combat logged and your doppel died!");
		} else {
			Double dmg = player.getHealth() - combatdoppel.getHealth();
			player.damage(dmg);
			player.sendMessage(ChatColor.RED + "Whoops - you combat logged and your doppel took " + dmg + " damage!");
			combatdoppel.damage(20);
		}
		doppellist.remove(player.getUniqueId());
	}

	/////////////////////////////////////////////////////////////////////////////
	//
	//	ListenForChunkUnloadEvent
	//	
	//
	
	public boolean doesChunkHaveDoppel(Chunk chunk) {
		return doppellist.values().stream().anyMatch(doppel -> Bukkit.getEntity(doppel).getLocation().getChunk().equals(chunk));
	}

}
