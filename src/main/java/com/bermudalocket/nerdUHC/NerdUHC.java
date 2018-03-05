package com.bermudalocket.nerdUHC;

import com.bermudalocket.nerdUHC.match.CombatLogger;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.bermudalocket.nerdUHC.match.MatchHandler;
import com.bermudalocket.nerdUHC.commands.Commands;
import com.bermudalocket.nerdUHC.listeners.PersistentListener;

//-------------------------------------------------------------------------------

public class NerdUHC extends JavaPlugin {

	public static NerdUHC plugin;

	public Configuration config;

	public MatchHandler matchHandler;

	public CombatLogger combatLogger;


	// -------------------------------------------------------------------------------

	@Override
	public void onEnable() {
		plugin = this;
		config = new Configuration();
		matchHandler = new MatchHandler();
		combatLogger = new CombatLogger();
		new PersistentListener();
		new Commands();
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}
	
}
