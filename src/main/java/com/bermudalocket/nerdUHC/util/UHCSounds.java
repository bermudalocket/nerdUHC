package com.bermudalocket.nerdUHC.util;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum UHCSounds {
	
	DING(Sound.BLOCK_NOTE_CHIME),
	OOPS(Sound.ENTITY_ITEM_BREAK),
	TIMERTICK(Sound.BLOCK_NOTE_HAT),
	JOINTEAM(Sound.ITEM_ARMOR_EQUIP_CHAIN),
	MATCHSTART(Sound.ENTITY_WITHER_SPAWN),
	MATCHEND(Sound.ENTITY_ENDERDRAGON_DEATH),
	DEATHMATCHSTART(Sound.ENTITY_WITHER_SPAWN),
	PVP_ENABLED(Sound.ENTITY_ENDERDRAGON_GROWL),
	PLAYERDEATH(Sound.ENTITY_LIGHTNING_THUNDER);
	
	private final Sound s;
	
	UHCSounds(Sound s) {
		this.s = s;
	}

	public void playSound(Player p) {
		p.playSound(p.getLocation(), s, 10, 1);
	}
	
	public void playSound() {
		Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), s, 10, 1));
	}
	
}