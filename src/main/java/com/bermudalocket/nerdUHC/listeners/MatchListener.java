package com.bermudalocket.nerdUHC.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.bermudalocket.nerdUHC.NerdUHC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCSound;

public class MatchListener implements Listener {

	private final NerdUHC plugin;
	private final UHCMatch match;
	
	private final HashMap<UUID, Long> specialItemCooldown = new HashMap<>();

	// ----------------------------------------------------------------

	public MatchListener(UHCMatch match) {
		this.plugin = NerdUHC.plugin;
		this.match = match;
	}

	public void startListening() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void stopListening() {
		HandlerList.unregisterAll(this);
	}

	// ----------------------------------------------------------------
	
	@EventHandler
	public void onRightClickWithItem(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() != Action.RIGHT_CLICK_AIR || p.getInventory().getItemInMainHand() == null) {
			return;
		}
		Material clickedItem = p.getInventory().getItemInMainHand().getType();
		if (clickedItem == Material.COMPASS || clickedItem == Material.WATCH) {
			if (specialItemCooldown.containsKey(p.getUniqueId())) {
				if (specialItemCooldown.get(p.getUniqueId()) > System.currentTimeMillis() - 10000) return;
			}
			specialItemCooldown.put(p.getUniqueId(), System.currentTimeMillis());
			if (clickedItem == Material.COMPASS) {
				showTeammateInfo(p);
			} else if (clickedItem == Material.WATCH) {
				showWorldBorderInfo(p);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() == null || e.getDamager() == null || e.getEntity() == e.getDamager()) {
			return;
		}
		if (e.getEntity() instanceof Player) { 
			plugin.combatLogger.combatLog((Player) e.getEntity());
		}
		if (e.getDamager() instanceof Player) { 
			plugin.combatLogger.combatLog((Player) e.getDamager());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (match.getMatchState() == UHCMatchState.PREGAME) {
			return;
		}
		if (plugin.combatLogger.isPlayerTagged(p)) {
			plugin.combatLogger.spawnDoppel(p);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		match.getScoreboardHandler().removePlayerFromTeam(p, true);
		UHCSound.PLAYERDEATH.playSound();
		match.checkForMatchEnd();
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		Entity entity = e.getEntity();
		
		if (entity instanceof Player) return;
		if (plugin.combatLogger.isDoppel(entity)) {
			UUID playerUUID = plugin.combatLogger.getPlayerFromDoppel(entity);
			if (playerUUID == null) return;

			plugin.combatLogger.addToDeathQueue(playerUUID);

			e.getDrops().clear();
			e.setDroppedExp(plugin.combatLogger.getExp(playerUUID));
			
			for (ItemStack i : plugin.combatLogger.getInventory(playerUUID).getContents()) {
				e.getDrops().add(i);
			}
			for (ItemStack i : plugin.combatLogger.getInventory(playerUUID).getArmorContents()) {
				e.getDrops().add(i);
			}
		} else {
			removeGhastTears(e);
		}
	}

	// ----------------------------------------------------------------

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
	
	private void showWorldBorderInfo(Player p) {
		int pX = Math.abs(p.getLocation().getBlockX());
		int pZ = Math.abs(p.getLocation().getBlockZ());
		int worldBorderSize = match.getWorldBorder().getSize()/2;
		int distX = worldBorderSize - pX;
		int distZ = worldBorderSize - pZ;
		int distance = (distX < distZ) ? distX : distZ;
		int shrinkRate = match.getWorldBorder().getShrinkRate();
		if (shrinkRate == 0) {
			p.sendMessage(String.format("%s%sThe world border is %s blocks away and is not currently shrinking!",
					ChatColor.GRAY, ChatColor.ITALIC, distance));
		} else {
			int timeToArrival = (distX < distZ) ? distX/shrinkRate : distZ/shrinkRate;
			p.sendMessage(String.format("%s%sThe world border is %s blocks away and will arrive in %s seconds!",
				ChatColor.GRAY, ChatColor.ITALIC, distance, timeToArrival));
		}
	}
	
	private void showTeammateInfo(Player p) {
		if (!match.getScoreboardHandler().hasTeam(p)) {
			p.sendMessage(String.format("%s%sLooks like you're all alone out there :(", ChatColor.GRAY, ChatColor.ITALIC));
			return;
		}
		teamSearch: for (String playerName : match.getScoreboard().getEntryTeam(p.getName()).getEntries()) {
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				String playerDisplayName = onlinePlayer.getDisplayName();
				if (playerName.equals(p.getName())) {
					continue teamSearch;
				} else if (playerName.equals(onlinePlayer.getName())) {
					if (p.getWorld() != onlinePlayer.getWorld()) {
						p.sendMessage(String.format("%s%s %s is far away (%s)", ChatColor.GRAY, ChatColor.ITALIC,
								playerDisplayName, onlinePlayer.getWorld().getName()));
					} else {
						int distance = (int) p.getLocation().distanceSquared(onlinePlayer.getLocation());
						String relDir = getRelativeDirection(p.getLocation(), onlinePlayer.getLocation());
						p.sendMessage(String.format("%s%s %s is %i blocks away (%s)", ChatColor.GRAY, ChatColor.ITALIC,
								playerDisplayName, distance, relDir));
					}
					continue teamSearch;
				}
				p.sendMessage(String.format("%s%s %s appears to be offline", ChatColor.GRAY, ChatColor.ITALIC,
						onlinePlayer.getDisplayName()));
			}
		}
		
	}
	
	/**
	 * Gets the relative direction of locB **from** locA
	 * @param locA First location
	 * @param locB Second location
	 * @return Relative cardinal direction (NW, NE, SW, SE)
	 */
	private String getRelativeDirection(Location locA, Location locB) {
		int xA = locA.getBlockX();
		int zA = locA.getBlockZ();
		int xB = locB.getBlockX();
		int zB = locB.getBlockZ();
		StringBuilder relativeDir = new StringBuilder();
		if (zB < zA) {
			// north
			relativeDir.append("N");
		} else {
			relativeDir.append("S");
		}
		if (xB < xA) {
			// west
			relativeDir.append("W");
		} else {
			relativeDir.append("E");
		}
		return relativeDir.toString();
	}

}
