package com.bermudalocket.nerdUHC;

import com.bermudalocket.nerdUHC.gui.GUIHandler;
import com.bermudalocket.nerdUHC.match.CombatLogger;
import com.bermudalocket.nerdUHC.player.PlayerHandler;
import com.bermudalocket.nerdUHC.scoreboard.ScoreboardHandler;
import com.bermudalocket.nerdUHC.thread.ThreadHandler;
import org.bukkit.plugin.java.JavaPlugin;

import com.bermudalocket.nerdUHC.match.MatchHandler;
import com.bermudalocket.nerdUHC.commands.Commands;
import com.bermudalocket.nerdUHC.listeners.PersistentListener;

public class NerdUHC extends JavaPlugin {

	public static NerdUHC PLUGIN;

	public static Configuration CONFIG;

	public static MatchHandler MATCH_HANDLER;

	public static CombatLogger COMBAT_LOGGER;

	public static ThreadHandler THREAD_HANDLER;

	public static ScoreboardHandler SCOREBOARD_HANDLER;

	public static PlayerHandler PLAYER_HANDLER;

	public static GUIHandler GUI_HANDLER;

	@Override
	public void onEnable() {
		PLUGIN = this;
		CONFIG = new Configuration();
		SCOREBOARD_HANDLER = new ScoreboardHandler();
		GUI_HANDLER = new GUIHandler();
		COMBAT_LOGGER = new CombatLogger();
		THREAD_HANDLER = new ThreadHandler();
		PLAYER_HANDLER = new PlayerHandler();
		MATCH_HANDLER = new MatchHandler();
		new PersistentListener();
		new Commands();
	}

}
