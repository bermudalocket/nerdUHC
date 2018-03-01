package com.bermudalocket.nerdUHC.listeners;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import com.bermudalocket.nerdUHC.modules.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCSound;

public class MatchListener implements Listener {

	private final UHCMatch match;

	// ----------------------------------------------------------------

	public MatchListener(UHCMatch match) {
		this.match = match;
	}

	// ----------------------------------------------------------------

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() == null || e.getDamager() == null || e.getEntity() == e.getDamager()) {
			return;
		}
		if (e.getEntity() instanceof Player) { 
			match.getCombatLogger().combatLog((Player) e.getEntity());
		}
		if (e.getDamager() instanceof Player) { 
			match.getCombatLogger().combatLog((Player) e.getDamager());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (match.getMatchState() == UHCMatchState.PREGAME) {
			return;
		}
		if (match.getCombatLogger().isPlayerTagged(p)) {
			match.getCombatLogger().spawnDoppel(p);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();

		if (p.getGameMode() == GameMode.SPECTATOR) {
			e.setDeathMessage("");
			return;
		} else {
			if (!e.getDeathMessage().contains("combat logging!")) e.setDeathMessage(handleDeathMessage(e));
		}

		Team t = match.getScoreboard().getEntryTeam(p.getName());
		if (t != null) t.removeEntry(p.getName());
		match.getScoreboardHandler().pruneTeams();

		p.setGameMode(GameMode.SPECTATOR);
		UHCSound.PLAYERDEATH.playSound();

		if (match.getCombatLogger().alreadyDroppedInv(p)) {
			e.setDroppedExp(0);
			e.getDrops().clear();
		}

		checkForMatchEnd();
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		Entity entity = e.getEntity();
		
		if (entity instanceof Player) return;
		if (match.getCombatLogger().isDoppel(entity)) {
			UUID playerUUID = match.getCombatLogger().getPlayerFromDoppel(entity);
			if (playerUUID == null) return;
			
			match.getCombatLogger().addToDeathQueue(playerUUID);

			e.getDrops().clear();
			e.setDroppedExp(match.getCombatLogger().getExp(playerUUID));
			
			for (ItemStack i : match.getCombatLogger().getInventory(playerUUID).getContents()) {
				e.getDrops().add(i);
			}
			for (ItemStack i : match.getCombatLogger().getInventory(playerUUID).getArmorContents()) {
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

	private void checkForMatchEnd() {
		if (match.getGameMode() == UHCGameMode.SOLO) {
			if (match.getScoreboard().getEntries().size() == 1) {
				OfflinePlayer lastplayer = (OfflinePlayer) match.getScoreboard().getEntries().toArray()[0];
				match.transitionToEnd(lastplayer.getName() + " wins!");
			}
		} else if (match.getGameMode() == UHCGameMode.TEAM) {
			if (match.getScoreboard().getTeams().size() == 1) {
				Team lastteam = (Team) match.getScoreboard().getTeams().toArray()[0];
				match.transitionToEnd("The " + lastteam.getDisplayName() + " win!");
			}
		}
	}
	
	private String handleDeathMessage(PlayerDeathEvent e) {
		Player p = e.getEntity();

		if (p.getGameMode() == GameMode.SPECTATOR) return null;
		
		String msg = null;
		if (p.getLastDamageCause() != null) {
			if (p.getKiller() != null) {
				if (p.getKiller() instanceof Player) {
					Player killer = p.getKiller();
					if (killer.getInventory().getItemInMainHand() != null) {
						String weapon = killer.getInventory().getItemInMainHand().getType().toString().replaceAll("_", " ");
						msg = p.getDisplayName() + ChatColor.WHITE + " fell to " + killer.getDisplayName() + ChatColor.WHITE + " with " + weapon;
					} else {
						msg = p.getDisplayName() + ChatColor.WHITE + " fell to " + killer.getDisplayName();
					}
				} else if (p.getKiller() instanceof Monster) {
					msg = p.getDisplayName() + ChatColor.WHITE + " fell to " + p.getKiller().getName();
				} else {
					msg = p.getDisplayName() + ChatColor.WHITE + " was expertly assassinated";
				}
			}
		} else {
			msg = p.getDisplayName() + " disappeared into thin air, never to be seen again.";
		}
		return msg;
	}
	
}
