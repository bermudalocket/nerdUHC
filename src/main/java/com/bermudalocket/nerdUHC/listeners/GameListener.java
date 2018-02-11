package com.bermudalocket.nerdUHC.listeners;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import com.bermudalocket.nerdUHC.modules.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCSound;

@SuppressWarnings("deprecation")
public class GameListener implements Listener {

	private UHCMatch match;

	public GameListener(UHCMatch match) {
		this.match = match;
	}
	
	//
	// Combat logger
	//
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

		if (e.getEntity() == null || e.getDamager() == null || e.getEntity() == e.getDamager()) return;

		if (e.getEntity() instanceof Player) match.getCombatLogger().combatLog((Player) e.getEntity());

		if (e.getDamager() instanceof Player) match.getCombatLogger().combatLog((Player) e.getDamager());

		if (match.isFrozen()) e.setCancelled(true);
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		if (match.getMatchState() == UHCMatchState.PREGAME) return;
		
		if (match.getCombatLogger().isPlayerTagged(p)) {
			
			match.getCombatLogger().logInventory(p);
			match.getCombatLogger().spawnDoppel(p);
			
		}
	}

	//
	// Players
	//

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		
		Player p = e.getEntity();

		//
		// Death message
		
		if (p.getGameMode() == GameMode.SPECTATOR) {
			e.setDeathMessage("");
			return;
		}

		String msg = handleDeathMessage(e);
		if (msg != null) {
			e.setDeathMessage(handleDeathMessage(e));
		}
		
		//
		// Remove from team

		Team t = match.getScoreboard().getPlayerTeam(p);
		if (t != null) t.removePlayer(p);
		
		//
		// Update scoreboard

		match.getScoreboardHandler().pruneTeams();
		match.getScoreboardHandler().refresh();
		
		//
		// Set player to spectator and play the death sound

		p.setGameMode(GameMode.SPECTATOR);
		UHCSound.PLAYERDEATH.playSound();
		
		//
		// If player already dropped their inventory via doppel, don't drop it again

		if (match.getCombatLogger().alreadyDroppedInv(p)) {
			e.setDroppedExp(0);
			e.getDrops().clear();
		}
		
		//
		// Check if the match should end -- is there only one player/team left?

		if (match.getGameMode() == UHCGameMode.SOLO) {
			if (match.getScoreboard().getPlayers().size() == 1) {
				OfflinePlayer lastplayer = (OfflinePlayer) match.getScoreboard().getPlayers().toArray()[0];
				match.transitionToEnd(lastplayer.getName() + " wins!");
			}
		} else if (match.getGameMode() == UHCGameMode.TEAM) {
			if (match.getScoreboard().getTeams().size() == 1) {
				Team lastteam = (Team) match.getScoreboard().getTeams().toArray()[0];
				match.transitionToEnd("The " + lastteam.getDisplayName() + " win!");
			}
		}
		
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (e.getPlayer().hasPermission("nerduhc.gamemaster"))
			return;

		Player p = e.getPlayer();

		if (match.isFrozen()) {
			if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
				if (!e.getFrom().toVector().equals(e.getTo().toVector())) {
					e.getTo().setDirection(e.getFrom().toVector());
				}
			}
		}
	}
	
	//
	// Other entity stuff
	//

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		
		if (e.getEntity() == null || !(e.getEntity() instanceof Player)) {
			return;
		}
		
		if (match.isFrozen()) {
			e.setCancelled(true);
		}
		
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		
		Entity entity = e.getEntity();
		
		if (entity instanceof Player) return;

		if (match.getCombatLogger().isDoppel(entity)) {
			
			UUID puuid = match.getCombatLogger().getPlayerFromDoppel(entity);
			if (puuid == null) return;
			
			match.getCombatLogger().addToDeathQueue(puuid);

			e.getDrops().clear();
			e.setDroppedExp(match.getCombatLogger().getExp(puuid));
			
			for (ItemStack i : match.getCombatLogger().getInventory(puuid).getContents()) {
				e.getDrops().add(i);
			}
			for (ItemStack i : match.getCombatLogger().getInventory(puuid).getArmorContents()) {
				e.getDrops().add(i);
			}
		} else {
			
			// no ghast tears
			if (e.getEntityType() == EntityType.GHAST) {
				for (int i = 0; i < e.getDrops().size(); i++) {
					if (e.getDrops().get(i).getType() == Material.GHAST_TEAR) {
						e.getDrops().remove(i);
						e.getDrops().add(new ItemStack(Material.GOLD_INGOT, 1));
					}
				}
			}
			
		}
	}
	
	//
	//
	//
	
	public String handleDeathMessage(PlayerDeathEvent e) {
		Player p = e.getEntity();
		
		String msg = null;
		if (p.getLastDamageCause() != null) {
			if (p.getKiller() != null) {
				if (p.getKiller() instanceof Player) {
					Player killer = (Player) p.getKiller();
					if (killer.getItemInHand() != null) {
						String weapon = killer.getItemInHand().getType().toString().replaceAll("_", " ");
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
		return (msg == null) ? null : msg;
	}
	
}
