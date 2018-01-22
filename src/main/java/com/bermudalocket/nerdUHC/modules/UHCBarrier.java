package com.bermudalocket.nerdUHC.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.bermudalocket.nerdUHC.NerdUHC;

public class UHCBarrier {
	
	private NerdUHC plugin;
	private UHCMatch match;
	
	public UHCBarrier(NerdUHC plugin, UHCMatch match) {
		this.plugin = plugin;
		this.match = match;
		drawBarrier(true);
	}
	
	public void drawBarrier(boolean draw) {
		
		World world = match.getWorld();
		
		int x = plugin.CONFIG.SPAWN_X;
		int y = plugin.CONFIG.SPAWN_Y;
		int z = plugin.CONFIG.SPAWN_Z;
		
		int radius = plugin.CONFIG.SPAWN_BARRIER_RADIUS;
		Material barriertype = plugin.CONFIG.SPAWN_BARRIER_BLOCK;
		
		Location center = new Location(world, x, y, z);
		Location point = new Location(world, 0, 0, 0);
		
		Material onlyreplace;
		Material replacewith;
		if (draw) {
			onlyreplace = Material.AIR;
			replacewith = barriertype;
		} else {
			onlyreplace = barriertype;
			replacewith = Material.AIR;
		}
		
		if (radius <= 0) return;

		for (int i = -radius - 1; i < radius + 1; i++) {
			for (int j = -radius - 1; j < radius + 1; j++) {
				for (int k = -radius - 1; k < radius + 1; k++) {
					point.setX(x+i);
					point.setY(y+j);
					point.setZ(z+k);
					Material lookingat = world.getBlockAt(point).getType();
					if (Math.abs((int) Math.round(center.distanceSquared(point)) - radius*radius) <= radius) {
						if (lookingat == onlyreplace) {
							world.getBlockAt(point).setType(replacewith);
						}
					}
				}
			}
		}
	}

}
