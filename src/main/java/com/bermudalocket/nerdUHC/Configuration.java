package com.bermudalocket.nerdUHC;

public class Configuration {
	
	private nerdUHC plugin;
	
	public String HEALTH_DISPLAY_SLOT;
	
	public Configuration(nerdUHC plugin) {
		this.plugin = plugin;
		plugin.saveDefaultConfig();
	}
	
	public void reload() {
		plugin.reloadConfig();
		HEALTH_DISPLAY_SLOT = plugin.getConfig().getString("health-display-slot", "PLAYER_LIST");
	}

}
