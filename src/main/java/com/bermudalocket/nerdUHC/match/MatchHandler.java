package com.bermudalocket.nerdUHC.match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.events.MatchStateChangeEvent;
import com.bermudalocket.nerdUHC.events.MatchTimerTickEvent;
import com.bermudalocket.nerdUHC.events.PlayerChangeTeamEvent;
import com.bermudalocket.nerdUHC.modules.UHCGameMode;
import com.bermudalocket.nerdUHC.modules.UHCMatchState;
import com.bermudalocket.nerdUHC.modules.UHCPlayer;
import com.bermudalocket.nerdUHC.modules.UHCSound;
import com.bermudalocket.nerdUHC.modules.UHCTeam;
import com.bermudalocket.nerdUHC.scoreboards.ScoreboardTimer;

public class MatchHandler implements Listener {

	private NerdUHC plugin;
	private ConsoleCommandSender console;

	private long timeend;
	private boolean playersfrozen;

	private UHCMatchState state;
	private UHCGameMode mode;
	private UHCTeam spectator;

	private HashMap<UUID, UHCPlayer> playerlist = new HashMap<UUID, UHCPlayer>();
	private HashMap<String, UHCTeam> teamlist = new HashMap<String, UHCTeam>();
	private List<UHCPlayer> winnerlist = new ArrayList<UHCPlayer>();

	private ScoreboardTimer scoreboardTimer;
	private MatchStartCountdownTimer matchStartCountdownTimer;
	private MatchEndTimer matchEndTimer;

	// ------------------------------------------------------------------------
	/**
	 * The constructor for MatchHandler.
	 * 
	 * @param plugin
	 *            - the NerdUHC plugin instance
	 */
	public MatchHandler(NerdUHC plugin) {
		this.plugin = plugin;
		this.console = Bukkit.getConsoleSender();

		this.timeend = 0;
		this.playersfrozen = false;

		this.state = UHCMatchState.PREGAME;
		this.mode = plugin.CONFIG.UHC_GAME_MODE;

		this.scoreboardTimer = new ScoreboardTimer(plugin);
		this.matchStartCountdownTimer = new MatchStartCountdownTimer(plugin);
		this.matchEndTimer = new MatchEndTimer(plugin);

		createTeams();
	}

	// ------------------------------------------------------------------------
	/**
	 * Plays a sound every time a timer ticks
	 * 
	 * @param e
	 */
	@EventHandler
	public void onTimerTick(MatchTimerTickEvent e) {
		playSound(UHCSound.TIMERTICK.sound());
	}

	/**
	 * Handles match state changes.
	 * 
	 * @param e
	 *            the MatchStateChangeEvent
	 */
	@EventHandler
	public void onMatchStateChange(MatchStateChangeEvent e) {
		this.state = e.getState();
		if (state.equals(UHCMatchState.PREGAME)) {

		} else if (state.equals(UHCMatchState.INPROGRESS) && e.getLastState().equals(UHCMatchState.PREGAME)) {
			matchStartCountdownTimer.run();
		} else if (state.equals(UHCMatchState.DEATHMATCH)) {
			startDeathmatch();
		} else if (state.equals(UHCMatchState.END)) {
			for (UHCPlayer p : playerlist.values()) {
				if (p.isAlive())
					winnerlist.add(p);
			}
			matchEndTimer.run(winnerlist);
		}
	}

	/**
	 * Upon player join, registers the player if they do not exist in the match
	 * list. If the player does exist, this reconfigures their team colors, which
	 * are not saved across a log in/out.
	 * 
	 * @param e
	 *            the PlayerJoinEvent
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if (playerExists(player)) {
			UHCPlayer p = getPlayer(player);
			player.setPlayerListName(p.getColor() + p.getName());
			player.setDisplayName(p.getColor() + p.getName() + ChatColor.WHITE);
		} else {
			UUID p = player.getUniqueId();
			player.setGameMode(GameMode.SURVIVAL);
			playerlist.put(p, new UHCPlayer(p));
		}
	}

	/**
	 * Handles a player changing their team
	 * 
	 * @param e
	 *            the PlayerChangeTeamEvent
	 */
	@EventHandler
	public void onPlayerChangeTeam(PlayerChangeTeamEvent e) {
		UHCPlayer p = e.getPlayer();
		UHCTeam newteam = e.getTeam();

		e.getPlayer().setTeam(newteam);
		newteam.add(p);

		if (e.getTeam().getName().equalsIgnoreCase("SPECTATOR")) {
			p.bukkitPlayer().setGameMode(GameMode.SPECTATOR);
		}

		if (e.getOldTeam() != null) {
			e.getOldTeam().remove(p);
			if (e.getOldTeam().getName().equalsIgnoreCase("SPECTATOR")) {
				p.bukkitPlayer().setGameMode(GameMode.SURVIVAL);
			}
		}
		p.bukkitPlayer().sendMessage("You joined the " + newteam.getDisplayName() + " team!");
		playSound(UHCSound.JOINTEAM.sound(), p.bukkitPlayer());
	}

	/**
	 * Handles a player's death by announcing it, setting their Alive boolean to
	 * false, and switching their gamemode to SPECTATOR
	 * 
	 * @param e
	 *            the PlayerDeathEvent
	 */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!isGameStarted())
			return;
		if (playerExists(e.getEntity().getUniqueId())) {
			UHCPlayer p = getPlayer(e.getEntity().getUniqueId());
			if (p.isAlive()) {
				if (allowTeams()) {
					e.setDeathMessage("[" + p.getTeam().getDisplayName() + "] " + p.getName() + " died.");
				} else {
					e.setDeathMessage(ChatColor.RED + p.getName() + " died.");
				}
				p.setAlive(false);
				e.getEntity().setGameMode(GameMode.SPECTATOR);
				playSound(UHCSound.PLAYERDEATH.sound());
			}
		}
	}

	/**
	 * Freezes a player's position if the frozen state is currently true
	 * 
	 * @param e
	 *            the PlayerMoveEvent
	 */
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (arePlayersFrozen() && playerExists(e.getPlayer().getUniqueId())) {
			if (!e.getFrom().toVector().equals(e.getTo().toVector())) {
				e.getTo().setDirection(e.getFrom().toVector());
			}
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Gets the current match state.
	 * 
	 * @return the current UHCMatchState
	 */
	public UHCMatchState getMatchState() {
		return state;
	}

	/**
	 * A method to create teams based on the current UHCGameMode.
	 */
	private void createTeams() {
		switch (this.mode) {
		default:
		case SOLO:
			UHCTeam alive = new UHCTeam(this, plugin.CONFIG.ALIVE_TEAM_NAME, ChatColor.WHITE,
					plugin.CONFIG.MAX_TEAM_SIZE, plugin.CONFIG.ALLOW_FRIENDLY_FIRE);
			UHCTeam dead = new UHCTeam(this, plugin.CONFIG.DEAD_TEAM_NAME, ChatColor.GRAY, plugin.CONFIG.MAX_TEAM_SIZE,
					false);
			teamlist.put(plugin.CONFIG.ALIVE_TEAM_NAME, alive);
			teamlist.put(plugin.CONFIG.DEAD_TEAM_NAME, dead);
			break;
		case TEAM:
			plugin.CONFIG.getRawTeamList().forEach(team -> {
				String teamname = team.get("name").toString().toUpperCase();
				String teamcolor = team.get("color").toString();
				ChatColor color;
				try {
					color = ChatColor.valueOf(teamcolor);
				} catch (Exception f) {
					color = ChatColor.STRIKETHROUGH;
					plugin.getLogger().info("Config error: Invalid color option for team " + teamname);
				}
				teamlist.put(teamname, new UHCTeam(this, teamname, color, plugin.CONFIG.MAX_TEAM_SIZE,
						plugin.CONFIG.ALLOW_FRIENDLY_FIRE));
			});
			break;
		}
		this.spectator = new UHCTeam(this, "SPECTATOR", ChatColor.GRAY, 999, false);
		teamlist.put("SPECTATOR", spectator);
	}

	/*
	 * Players
	 */

	public void registerPlayer(UUID player) {
		playerlist.put(player, new UHCPlayer(player));
	}

	public void registerPlayer(UUID player, UHCTeam team) {
		playerlist.put(player, new UHCPlayer(player, team));
	}

	public boolean playerExists(UUID player) {
		return playerlist.containsKey(player);
	}
	
	public boolean playerExists(Player player) {
		return playerExists(player.getUniqueId());
	}

	public UHCPlayer getPlayer(UUID player) {
		return playerlist.get(player);
	}

	public UHCPlayer getPlayer(String player) {
		return playerlist.values().stream().filter(p -> p.getName().equalsIgnoreCase(player))
				.collect(Collectors.toList()).get(0);
	}
	
	public UHCPlayer getPlayer(Player player) {
		return getPlayer(player.getUniqueId());
	}

	public void freezePlayers(boolean state) {
		playersfrozen = state;
	}

	public boolean arePlayersFrozen() {
		return playersfrozen;
	}

	/*
	 * Gamerules and other match parameters
	 */

	public void setGameRules() {
		plugin.CONFIG.GAMERULES.forEach(gamerule -> {
			String rule = gamerule.keySet().toArray()[0].toString();
			String value = gamerule.values().toArray()[0].toString();
			plugin.CONFIG.WORLD.setGameRuleValue(rule, value);
		});
	}

	public void setGameMode(UHCGameMode mode) {
		this.mode = mode;
	}

	public UHCGameMode getGameMode() {
		return mode;
	}

	public boolean isGameStarted() {
		return (this.state.equals(UHCMatchState.INPROGRESS) || this.state.equals(UHCMatchState.DEATHMATCH)) ? true
				: false;
	}

	public long getTimeEnd() {
		return timeend;
	}

	public void extendTime(int sec) {
		timeend += sec * 1000;
	}

	/*
	 * Teams
	 */

	public UHCTeam getTeam(String team) {
		return teamlist.get(team.toUpperCase());
	}

	public Collection<UHCTeam> getTeams() {
		return teamlist.values();
	}

	public boolean teamExists(String team) {
		return teamlist.containsKey(team.toUpperCase());
	}

	public boolean allowTeams() {
		return mode.equals(UHCGameMode.TEAM);
	}

	public UHCTeam getSpectatorTeam() {
		return this.spectator;
	}

	/*
	 * Start, stop
	 */

	public void startDeathmatch() {
		String target = "";
		for (UHCPlayer p : playerlist.values()) {
			if (p.isAlive())
				target += p.getName() + " ";
		}

		String spreadplayers = "spreadplayers " + plugin.CONFIG.SPAWN_X + " " + plugin.CONFIG.SPAWN_Z + " "
				+ plugin.CONFIG.DEATHMATCH_DIST_BTWN_PLAYERS + " " + plugin.CONFIG.DEATHMATCH_SPREAD_DIST_FROM_SPAWN
				+ " ";
		if (plugin.match.getGameMode().equals(UHCGameMode.TEAM)) {
			spreadplayers = spreadplayers + plugin.CONFIG.SPREAD_RESPECT_TEAMS + " ";
		} else {
			spreadplayers = spreadplayers + !plugin.CONFIG.SPREAD_RESPECT_TEAMS + " ";
		}
		final String spreadplayerscmd = spreadplayers + target;

		plugin.getServer().dispatchCommand(console, spreadplayerscmd);

		Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.RED + "Deathmatch!", null, 15, 60, 15));

		freezePlayers(false);
	}

	public void startUHC() {
		timeend = System.currentTimeMillis() + plugin.CONFIG.MATCH_DURATION * 60 * 1000;

		String target = "@a[x=" + plugin.CONFIG.SPAWN_X + ",y=" + plugin.CONFIG.SPAWN_Y + ",z=" + plugin.CONFIG.SPAWN_Z
				+ ",r=" + plugin.CONFIG.SPAWN_BARRIER_RADIUS + "]";

		String spreadplayers = "spreadplayers " + plugin.CONFIG.SPAWN_X + " " + plugin.CONFIG.SPAWN_Z + " "
				+ plugin.CONFIG.SPREAD_DIST_BTWN_PLAYERS + " " + plugin.CONFIG.SPREAD_DIST_FROM_SPAWN + " ";
		if (plugin.match.getGameMode().equals(UHCGameMode.TEAM)) {
			spreadplayers = spreadplayers + plugin.CONFIG.SPREAD_RESPECT_TEAMS + " ";
		} else {
			spreadplayers = spreadplayers + !plugin.CONFIG.SPREAD_RESPECT_TEAMS + " ";
		}
		final String spreadplayerscmd = spreadplayers + target;

		for (UHCPlayer p : playerlist.values()) {
			p.bukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20*1, 100, true));
			p.bukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20*1, 100, true));
			p.bukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60, 100, true));
		}

		plugin.CONFIG.WORLD.setTime(0);
		plugin.CONFIG.WORLD.setStorm(false);
		plugin.CONFIG.WORLD.setDifficulty(Difficulty.NORMAL);
		plugin.CONFIG.WORLD.setPVP(true);

		plugin.getServer().dispatchCommand(console, spreadplayerscmd);
		UHCSound.MATCHSTART.playSound();
		plugin.barrier.drawBarrier(false);
		this.scoreboardTimer.run();
		plugin.scoreboardHandler.showSidebar();
	}

	public int numberOfAlivePlayers() {
		int i = 0;
		for (UHCPlayer p : playerlist.values()) {
			if (p.isAlive())
				i++;
		}
		return i;
	}

	public void playSound(Sound sound) {
		playSound(sound, null);
	}

	public void playSound(Sound sound, Player p) {
		if (p == null) {
			Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), sound, 10, 1));
		} else {
			p.playSound(p.getLocation(), sound, 10, 2);
		}
	}

	public void playNote(Instrument i, Note n) {
		playNote(i, n, null);
	}

	public void playNote(Instrument i, Note n, Player p) {
		if (p == null) {
			Bukkit.getOnlinePlayers().forEach(player -> player.playNote(player.getLocation(), i, n));
		} else {
			p.playNote(p.getLocation(), i, n);
		}
	}

}
