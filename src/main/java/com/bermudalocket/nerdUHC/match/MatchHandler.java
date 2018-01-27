package com.bermudalocket.nerdUHC.match;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCMatch;

public class MatchHandler {

	private NerdUHC plugin;
	private ArrayList<UHCMatch> matches;

	public MatchHandler(NerdUHC plugin) {
		plugin.getLogger().info("Creating a new match...");
		this.plugin = plugin;
		this.matches = new ArrayList<UHCMatch>();
		UHCMatch match = new UHCMatch(plugin);
		matches.add(match);
		plugin.getLogger().info("New match created with ID " + match.toString());
	}
	
	public UHCMatch getMatch() {
		for (UHCMatch m : matches) {
			if (m.isActive()) return m;
		}
		return matches.get(0);
	}
	
	public void nextMatch() {
		matches.add(new UHCMatch(plugin, getMatch()));
	}
	
	public UHCMatch getMatchByPlayer(Player p) {
		for (UHCMatch m : matches) {
			for (UUID player : m.getPlayers()) {
				if (player.equals(p.getUniqueId())) return m;
			}
		}
		return null;
	}
	
	public UHCMatch getMatchByWorld(World w) {
		for (UHCMatch m : matches) {
			if (m.getWorld() == w) return m;
		}
		return null;
	}
	
	public UHCMatch getNextMatchByPosition(UHCMatch match) {
		return matches.get(matches.indexOf(match) + 1);
	}
	
	public void rotate(UHCMatch match) {
		getNextMatchByPosition(match).setActive();
		matches.remove(match);
	}

}
