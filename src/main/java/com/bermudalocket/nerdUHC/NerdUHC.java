package com.bermudalocket.nerdUHC;

import com.bermudalocket.nerdUHC.commands.Commands;
import com.bermudalocket.nerdUHC.match.CombatLogger;
import com.bermudalocket.nerdUHC.match.Match;
import com.bermudalocket.nerdUHC.match.MatchHandler;
import com.bermudalocket.nerdUHC.match.MatchState;
import com.bermudalocket.nerdUHC.modules.UHCLibrary;
import com.bermudalocket.nerdUHC.modules.UHCSound;
import com.bermudalocket.nerdUHC.player.PlayerHandler;
import com.bermudalocket.nerdUHC.scoreboards.ScoreboardHandler;
import com.bermudalocket.nerdUHC.team.TeamHandler;
import com.bermudalocket.nerdUHC.team.UHCTeam;
import com.bermudalocket.nerdUHC.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

//-------------------------------------------------------------------------------

public class NerdUHC extends JavaPlugin implements Listener {

	/**
	 * Plugin instance accessible as a singleton.
	 */
	public static NerdUHC PLUGIN;

	/**
	 * Configuration manager as a singleton.
	 */
	public static final Configuration CONFIG = new Configuration();

	/**
	 * Match manager as a singleton.
	 */
	public static final MatchHandler MATCH_HANDLER = new MatchHandler();

	/**
	 * Team manager as a singleton.
	 */
	public static final TeamHandler TEAM_HANDLER = new TeamHandler();

	/**
	 * Player manager as a singleton.
	 */
	public static final PlayerHandler PLAYER_HANDLER = new PlayerHandler();

	/**
	 * Scoreboard manager as a singleton.
	 */
	public static final ScoreboardHandler SCOREBOARD_HANDLER = new ScoreboardHandler();

	/**
	 * Helpful, useful, convenience utilities as a singleton.
	 */
	public static final Util UTIL = new Util();

	/**
	 * Combat logging manager as a singleton.
	 */
	private static final CombatLogger COMBAT_LOGGER = new CombatLogger();

	// -------------------------------------------------------------------------

	/**
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	@Override
	public void onEnable() {
		PLUGIN = this;
		new Commands();
		if (savedStateExists()) loadState();
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	/**
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 */
	@Override
	public void onDisable() {
		if (MATCH_HANDLER.getMatch().getMatchState().isInProgress()) {
			saveState();
		}
	}

	private boolean savedStateExists() {
		File file = new File("savedstate");
		return file.exists();
	}

	private void saveState() {
		try {
			Serializer.saveObject(MATCH_HANDLER, "MATCH_HANDLER");
			Serializer.saveObject(TEAM_HANDLER, "TEAM_HANDLER");
			Serializer.saveObject(PLAYER_HANDLER, "PLAYER_HANDLER");
			Serializer.saveObject(SCOREBOARD_HANDLER, "SCOREBOARD_HANDLER");
			Serializer.saveObject(COMBAT_LOGGER, "COMBAT_LOGGER");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadState() {
		try {
			Object tryMatchHandler = Serializer.readObject("MATCH_HANDLER");
			if (!(tryMatchHandler instanceof MatchHandler)) throw new IOException();

			Object tryTeamHandler = Serializer.readObject("TEAM_HANDLER");
			if (!(tryTeamHandler instanceof TeamHandler)) throw new IOException();

			Object tryPlayerHandler = Serializer.readObject("PLAYER_HANDLER");
			if (!(tryPlayerHandler instanceof  PlayerHandler)) throw new IOException();

			Object tryScoreboardHandler = Serializer.readObject("SCOREBOARD_HANDLER");
			if (!(tryScoreboardHandler instanceof ScoreboardHandler)) throw new IOException();

			Object tryCombatLogger = Serializer.readObject("COMBAT_LOGGER");
			if (!(tryCombatLogger instanceof CombatLogger)) throw new IOException();

			MATCH_HANDLER.load((MatchHandler) tryMatchHandler);
			TEAM_HANDLER.load((TeamHandler) tryTeamHandler);
			PLAYER_HANDLER.load((PlayerHandler) tryPlayerHandler);
			SCOREBOARD_HANDLER.load((ScoreboardHandler) tryScoreboardHandler);
			//COMBAT_LOGGER.load((CombatLogger) tryCombatLogger);

			File file = new File("savedstate");
			if (file.exists()) {
				if (file.delete()) getLogger().info("Successfully deleted old saved state.");
			}
		} catch (Exception e) {
			getLogger().warning("Something went wrong loading a saved state.");
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * Replaces the player's username with their Team-colored variant
	 */
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		UHCTeam team = TEAM_HANDLER.getTeam(player);
		ChatColor color = (team == null) ? ChatColor.WHITE : team.getColor();

		for (Player onlinePlayer : getServer().getOnlinePlayers()) {
			onlinePlayer.sendMessage("<" + color + player.getName() + ChatColor.WHITE + "> " + event.getMessage());
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Match match = MATCH_HANDLER.getMatch();
		Player player = event.getPlayer();

		if (CONFIG.USE_SCOREBOARD) SCOREBOARD_HANDLER.update(player);

		PLAYER_HANDLER.formatDisplayName(player);

		if (match.getMatchState() == MatchState.PREGAME) {
			player.teleport(match.getSpawn());
			PLAYER_HANDLER.resetPlayer(player);
		} else {
			if (!TEAM_HANDLER.hasTeam(player)) PLAYER_HANDLER.makeSpectator(player);
		}

		// run some tasks that need to be run 1 tick after joining
		BukkitRunnable playerJoinTask = new BukkitRunnable() {
			@Override
			public void run() {
				UHCLibrary.LIB_WELCOME.rep(player, "%t", match.getGameMode().toString());
				COMBAT_LOGGER.reconcileDoppelWithPlayer(player);
			}
		};
		playerJoinTask.runTaskLater(this, 1);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		Match match = MATCH_HANDLER.getMatch();
		if (match.getMatchState() != MatchState.PREGAME) return;

		if (e.getEntity() instanceof Player) e.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
		Match match = MATCH_HANDLER.getMatch();
		if (match.getMatchState() != MatchState.PREGAME) return;

		if (e.getEntity() instanceof Player) e.setCancelled(true);
	}

	/**
	 * Stops mobs from spawning before the match has begun
	 * @param event The CreatureSpawnEvent event
	 */
	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent event) {
		Match match = MATCH_HANDLER.getMatch();
		if (match.getMatchState() != MatchState.PREGAME) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Match match = MATCH_HANDLER.getMatch();
		if (match.getMatchState() != MatchState.PREGAME) return;

		if (e.getTo().distanceSquared(e.getFrom().getWorld().getSpawnLocation())
				> CONFIG.SPAWN_BARRIER_RADIUS_SQUARED) {
			e.getPlayer().teleport(e.getFrom());
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() == null || event.getDamager() == null || event.getEntity() == event.getDamager()) {
			return;
		}
		if (event.getEntity() instanceof Player) {
			COMBAT_LOGGER.combatLog((Player) event.getEntity());
		}
		if (event.getDamager() instanceof Player) {
			COMBAT_LOGGER.combatLog((Player) event.getDamager());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		Match match = MATCH_HANDLER.getMatch();
		if (match.getMatchState() == MatchState.PREGAME) return;

		if (COMBAT_LOGGER.isPlayerTagged(player)) COMBAT_LOGGER.spawnDoppel(player);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		Match match = MATCH_HANDLER.getMatch();
		if (match.getMatchState() == MatchState.PREGAME || !PLAYER_HANDLER.isPlaying(player)) return;

		TEAM_HANDLER.removePlayerFromTeam(player);
		UHCSound.PLAYERDEATH.playSound();
		match.checkForMatchEnd();
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		Entity entity = e.getEntity();

		if (entity instanceof Player) return;
		if (COMBAT_LOGGER.isDoppel(entity)) {
			UUID playerUuid = COMBAT_LOGGER.getPlayerFromDoppel(entity);
			if (playerUuid == null) return;

			COMBAT_LOGGER.addToDeathQueue(playerUuid);

			e.getDrops().clear();
			e.setDroppedExp(COMBAT_LOGGER.getExp(playerUuid));

			for (ItemStack i : COMBAT_LOGGER.getInventory(playerUuid).getContents()) {
				e.getDrops().add(i);
			}
			for (ItemStack i : COMBAT_LOGGER.getInventory(playerUuid).getArmorContents()) {
				e.getDrops().add(i);
			}
		} else {
			UTIL.removeGhastTears(e);
		}
	}

}
