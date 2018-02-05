package com.bermudalocket.nerdUHC.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
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

public class GameListener implements Listener {

	private UHCMatch match;

	public GameListener(UHCMatch match) {
		this.match = match;
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

	@EventHandler
	@SuppressWarnings("deprecation")
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();

		if (p.getGameMode() == GameMode.SPECTATOR) {
			e.setDeathMessage(null);
			return;
		}

		String msg = handleDeathMessage(e);
		if (msg != null) {
			e.setDeathMessage(handleDeathMessage(e));
		}

		Team t = match.getScoreboard().getPlayerTeam(p);
		if (t != null) t.removePlayer(p);

		match.getScoreboardHandler().pruneTeams();
		match.getScoreboardHandler().refresh();

		p.setGameMode(GameMode.SPECTATOR);

		UHCSound.PLAYERDEATH.playSound();

		if (match.getCombatLogger().alreadyDroppedInv(p)) {
			e.setDroppedExp(0);
			e.getDrops().clear();
		}

		if (match.getGameMode() == UHCGameMode.SOLO) {
			if (match.getScoreboard().getPlayers().size() == 1) {
				OfflinePlayer lastplayer = (OfflinePlayer) match.getScoreboard().getPlayers().toArray()[0];
				Bukkit.getOnlinePlayers()
						.forEach(player -> player.sendTitle(lastplayer.getName() + " wins!", null, 20, 120, 20));
				match.transitionToEnd();
			}
		} else if (match.getGameMode() == UHCGameMode.TEAM) {
			int i = 0;
			for (Team team : match.getScoreboard().getTeams()) {
				if (team.getSize() > 0) i++;
			}
			if (i == 1) {
				Team lastteam = (Team) match.getScoreboard().getTeams().toArray()[0];
				Bukkit.getOnlinePlayers()
						.forEach(player -> player.sendTitle("The " + lastteam.getDisplayName() + " win!", null, 20, 120, 20));
				match.transitionToEnd();
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

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

		if (e.getEntity() == null || e.getDamager() == null || e.getEntity() == e.getDamager()) return;

		if (e.getEntity() instanceof Player) match.getCombatLogger().combatLog((Player) e.getEntity());

		if (e.getDamager() instanceof Player) match.getCombatLogger().combatLog((Player) e.getDamager());

		if (match.isFrozen()) e.setCancelled(true);
		
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		
		if (e.getEntity() == null) return;
		
		if (!(e.getEntity() instanceof Player)) return;
		
		if (match.isFrozen()) e.setCancelled(true);
		
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		
		if (e.getEntity() instanceof Player) return;

		if (match.getCombatLogger().isDoppel(e.getEntity())) {
			
			UUID puuid = match.getCombatLogger().getPlayerFromDoppel(e.getEntity());
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
		}
	}
	
	//
	//
	//
	
	@SuppressWarnings("deprecation")
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
