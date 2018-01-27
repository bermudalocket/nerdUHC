package com.bermudalocket.nerdUHC.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
import org.bukkit.scoreboard.Team;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCMatch;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCSound;

public class GameListener implements Listener {
	
	private NerdUHC plugin;
	private UHCMatch match;
	
	private final String LIB_IN_PROGRESS = ChatColor.GOLD + "A round is already in progress, so you will have to spectate.";
	
	public GameListener(NerdUHC plugin, UHCMatch match) {
		this.plugin = plugin;
		this.match = match;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UHCMatch match = plugin.matchHandler.getMatchByPlayer(p);
		if (match == null || match != this.match) return;
		
		if (match.getMatchState() == UHCMatchState.PREGAME) return;
		
		if (match.getCombatLogger().isPlayerTagged(p)) {
			match.getCombatLogger().spawnDoppel(p, p.getLocation());
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UHCMatch match = plugin.matchHandler.getMatchByPlayer(p);
		if (match == null) {
			UHCMatch currentmatch = plugin.matchHandler.getMatch();
			currentmatch.addPlayer(p);
			p.setDisplayName(ChatColor.GRAY + "" + ChatColor.ITALIC + p.getName() + ChatColor.WHITE);
			p.setGameMode(GameMode.SPECTATOR); //
			p.sendMessage(LIB_IN_PROGRESS);
		} else {
			if (match != this.match) return;
			Team t = match.getScoreboard().getEntryTeam(p.getName());
			p.setPlayerListName(t.getColor() + p.getName());
			p.setDisplayName(t.getColor() + p.getName() + ChatColor.WHITE);
			
			BukkitRunnable attemptReconcileDoppelTask = new BukkitRunnable() {
				@Override
				public void run() {
					match.getCombatLogger().reconcileDoppelWithPlayer(p);
				}
			};
			attemptReconcileDoppelTask.runTaskLater(plugin, 1);
			if (match.getMatchState() == UHCMatchState.PREGAME) {
				plugin.scoreboardHandler.showTeamCountCapacity(match);
			} else {
				plugin.scoreboardHandler.showKills(match);
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		UHCMatch match = plugin.matchHandler.getMatchByPlayer(p);
		if (match == null || match != this.match) return;
		
		plugin.scoreboardHandler.showKills(match);
		
		if (p.getGameMode() == GameMode.SPECTATOR) {
			e.setDeathMessage(null);
			return;
		}
		
		if (match.getGameMode() == UHCGameMode.TEAM) {
			Team t = match.getScoreboard().getEntryTeam(p.getName());
			e.setDeathMessage("[" + t.getDisplayName() + "] " + p.getName() + " died.");
		} else {
			e.setDeathMessage(ChatColor.RED + p.getName() + " died.");
		}
		p.setGameMode(GameMode.SPECTATOR);
		UHCSound.PLAYERDEATH.playSound();
		
		if (match.getCombatLogger().alreadyDroppedInv(p)) {
			e.setDroppedExp(0);
			e.getDrops().clear();
		}
		
		if (match.getMatchState() == UHCMatchState.DEATHMATCH) {
			Team last = match.getLastTeamStanding();
			if (last != null) {
				match.getPlayers().forEach(uuid -> {
					Player player = Bukkit.getPlayer(uuid);
					player.sendTitle(last.getDisplayName() + " wins!", null, 20, 180, 20);
				});
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (e.getPlayer().hasPermission("nerduhc.gamemaster")) return;
		
		Player p = e.getPlayer();
		UHCMatch match = plugin.matchHandler.getMatchByPlayer(p);
		if (match == null || match != this.match) return;
		
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

		Player p;
		if (e.getEntity() instanceof Player) { 
			p = (Player) e.getEntity();
		} else if(e.getDamager() instanceof Player) {
			p = (Player) e.getDamager();
		} else {
			return;
		}
	
		UHCMatch match = plugin.matchHandler.getMatchByPlayer(p);
		if (match == null || match != this.match) return;
		
		if (match.isFrozen() || !match.allowPVP()) e.setCancelled(true);
		match.getCombatLogger().combatLog(p);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() == null) return;
		if (!(e.getEntity() instanceof Player)) return;
	
		Player p = (Player) e.getEntity();
		UHCMatch match = plugin.matchHandler.getMatchByPlayer(p);
		if (match == null || match != this.match) return;
		if (match.isFrozen() || !match.allowPVP()) e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Player) return;
		
		UHCMatch match = plugin.matchHandler.getMatchByWorld(e.getEntity().getWorld());
		if (match == null || match != this.match) return;
		
		if (match.getCombatLogger().isDoppel(e.getEntity())) {
			Player p = match.getCombatLogger().getPlayerFromDoppel(e.getEntity());
			
			match.getCombatLogger().addToDeathQueue(p);
			
			e.getDrops().clear();
			e.setDroppedExp(Math.round(p.getExp()));
			for (ItemStack i : p.getInventory().getContents()) {
				e.getDrops().add(i);
			}
			for (ItemStack i : p.getInventory().getArmorContents()) {
				e.getDrops().add(i);
			}
		}
	}

}
