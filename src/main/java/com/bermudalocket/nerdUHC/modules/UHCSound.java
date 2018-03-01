package com.bermudalocket.nerdUHC.modules;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum UHCSound {
	
	DING(Sound.BLOCK_NOTE_CHIME),
	OOPS(Sound.ENTITY_ITEM_BREAK),
	TIMERTICK(Sound.BLOCK_NOTE_HAT),
	JOINTEAM(Sound.ITEM_ARMOR_EQUIP_CHAIN),
	MATCHSTART(Sound.ENTITY_WITHER_SPAWN),
	MATCHEND(Sound.ENTITY_ENDERDRAGON_DEATH),
	DEATHMATCHSTART(Sound.ENTITY_WITHER_SPAWN),
	PLAYERDEATH(Sound.ENTITY_LIGHTNING_THUNDER);
	
	private final Sound s;
	
	UHCSound(Sound s) { 
		this.s = s;
	}
	
	public Sound sound() {
		return s;
	}

	public void playSound(Player p) {
		p.playSound(p.getLocation(), s, 10, 1);
	}
	
	public void playSound() {
		Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), s, 10, 1));
	}
	
}