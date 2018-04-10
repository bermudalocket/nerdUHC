package com.bermudalocket.nerdUHC.team;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.util.Constants;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class UHCTeam {

	private String _name;
	private ChatColor _color;
	private int _maxSize;
	private boolean _isSpectator;

	private HashSet<UUID> _roster = new HashSet<>();

	UHCTeam(String teamName, ChatColor teamColor, int maxSize) {
		_name = teamName;
		_color = teamColor;
		_maxSize = maxSize;
	}

	public String getName() {
		return _name;
	}

	void add(Player player) {
		_roster.add(player.getUniqueId());
	}

	void remove(Player player) {
		_roster.remove(player.getUniqueId());
	}

	boolean contains(Player player) {
		return _roster.contains(player.getUniqueId());
	}

	public ChatColor getColor() {
		return _color;
	}

	boolean isFull() {
		return _roster.size() >= _maxSize;
	}

	boolean isSpectator() {
		return _isSpectator;
	}

	String getDisplayName() {
		return String.format("%s%s%s", _color, _name, Constants.RESET_COLOR);
	}

	public int size() {
		return _roster.size();
	}

}
