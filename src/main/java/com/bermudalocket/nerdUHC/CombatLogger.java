package com.bermudalocket.nerdUHC;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import com.bermudalocket.nerdUHC.NerdUHC;

/////////////////////////////////////////////////////////////////////////////
//
//	CombatLogger
//
//

public class CombatLogger {
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	ListenForEntityDamageByEntityEvent
	//
	//

	// ********************************************
	// Tags player and combatant adding them to
	// the taglist and lastattackerlist maps
	// ********************************************
	public void tagCombat(UUID first, UUID second) {
		
		Long timenow = System.currentTimeMillis();
		Entity firstentity = Bukkit.getEntity(first);
		Entity secondentity = Bukkit.getEntity(second);
	
		if ((first == null) || (second == null)) return;
		if (first == second) return;
		
		if (firstentity instanceof Player && secondentity instanceof Player) { 
			taglist.put(first, timenow);
			taglist.put(second,  timenow);
		} else if ((firstentity instanceof Creature)) { 
			taglist.put(second, timenow);
			lastattackerlist.put(second, first);
		} else if (secondentity instanceof Creature) {
			taglist.put(first,  timenow);
			lastattackerlist.put(first, second);
		}
		NerdUHC.PLUGIN.getLogger().info(taglist.toString());
		NerdUHC.PLUGIN.getLogger().info(lastattackerlist.toString());
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	ListenForEntityDeathEvent
	//	
	//
	
	// ********************************************
	// Checks if the UUID belongs to a doppel
	// ********************************************
	public boolean isDoppel(UUID doppel) {
		return doppellist.containsValue(doppel);
	}
	
	// ********************************************
	// GETTER for what doppel should drop upon death
	// ********************************************
	public ArrayList<ItemStack> getDoppelDrops(UUID player) {
		return doppeldrops.get(player);
	}
	
	// ********************************************
	// gets the player associated with a doppel
	// ********************************************
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
	
	// ********************************************
	// returns if player's tag is active
	// ********************************************
	public boolean isPlayerTagged(UUID player) {
		
		Long timenow = System.currentTimeMillis();
		Long playerlasttagged = taglist.get(player);	
		
		if (playerlasttagged == null) return false;
		
		Long tagexpires = playerlasttagged + NerdUHC.CONFIG.PLAYER_COMBAT_TAG_TIME*1000;
		
		if (timenow < tagexpires) {
			return true;
		} else {
			return false;
		}
		
	}
	
	// ********************************************
	// spawns a doppel and gives it all the
	// attributes of its player counterpart
	// ********************************************
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
		//combatdoppel.setFireTicks(20*20);
		
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
		// Doesn't currently work exactly right if chunk isn't loaded
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
	
	// ********************************************
	// checks if player has an active doppel
	// ********************************************
	public boolean doesPlayerHaveDoppel(UUID player) {
		
		return doppellist.containsKey(player);
	}
	
	// ********************************************
	// reconciles a player with its doppel, transf-
	// erring damage taken
	// ********************************************
	public void reconcileDoppelWithPlayer(UUID playeruuid) {
		
		Player player = Bukkit.getPlayer(playeruuid);
		UUID combatdoppelID = doppellist.get(player.getUniqueId());
		
		List<Entity> nearbyent = player.getNearbyEntities(3,3,3);
		if (nearbyent.isEmpty()) {
			// nothing around
		} else {
			List<Entity> match = nearbyent.stream().filter(ent -> ent.getUniqueId().equals(combatdoppelID)).collect(Collectors.toList());
			if (match.isEmpty()) {
				// doppel is gone or something
			} else {
				Entity combatdoppel = match.get(0);
            		if (!combatdoppel.isValid()) {
            			killPlayer(player);
            			player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 10, 1);
            			player.sendMessage(ChatColor.RED + "Whoops - you combat logged and your doppel died!");
            		} else {
            			Damageable combatdoppeldmg = (Damageable) combatdoppel;
            			Double dmg = player.getHealth() - combatdoppeldmg.getHealth();
            			player.setSaturation(0);
            			player.setHealth(player.getHealth() - dmg);
            			player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 10, 1);
            			player.sendMessage(ChatColor.RED + "Whoops - you combat logged and your doppel took " + ChatColor.BOLD + dmg + ChatColor.RESET + ChatColor.RED + " damage!");
            			combatdoppel.remove();
            		}
			}
			doppellist.remove(player.getUniqueId());
        		doppeldrops.remove(player.getUniqueId());
		}
	}
	
	public void killPlayer(Entity player) {
		@SuppressWarnings("deprecation")
		EntityDamageEvent e = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.VOID, 1000);
		Bukkit.getPluginManager().callEvent(e);
		if (e.isCancelled()) return;
		e.getEntity().setLastDamageCause(e);
		((Player) player).setHealth(0);
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	ListenForChunkUnloadEvent
	//	
	//
	
	public boolean doesChunkHaveDoppel(Chunk chunk) {
		return doppellist.values().stream().anyMatch(doppel -> Bukkit.getEntity(doppel).getLocation().getChunk().equals(chunk));
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//
	//	Fields
	//
	//
	
	// ********************************************
	// Map: tagged players -> tagged time
	// ********************************************
	private Map<UUID, Long> taglist = new HashMap<UUID, Long>();		// player UUID
	
	// ********************************************
	// Map: player -> player's doppel
	// ********************************************
	private Map<UUID, UUID> doppellist = new HashMap<UUID, UUID>();		// player UUID to entity UUID
	
	// ********************************************
	// Map: player -> list of items in inventory
	// ********************************************
	private Map<UUID, ArrayList<ItemStack>> doppeldrops = new HashMap<UUID, ArrayList<ItemStack>>();	// player UUID
	
	// ********************************************
	// Map: player -> player's last attacker
	// ********************************************
	private Map<UUID, UUID> lastattackerlist = new HashMap<UUID, UUID>();
	
}
