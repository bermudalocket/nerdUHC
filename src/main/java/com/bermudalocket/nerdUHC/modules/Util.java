package com.bermudalocket.nerdUHC.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Util {
	
	public  void drawBarrier(World world, int x, int y, int z, int radius, Material barriertype, Material onlyreplace) {
		if (radius <= 0) return;
		
		Location center = new Location(world, x, y, z);
		Location point = new Location(world, 0, 0, 0);
		
		for (int i = -radius - 1; i < radius + 1; i++) {
			for (int j = -radius - 1; j < radius + 1; j++) {
				for (int k = -radius - 1; k < radius + 1; k++) {
					point.setX(x+i);
					point.setY(y+j);
					point.setZ(z+k);
					Material lookingat = world.getBlockAt(point).getType();
					if (Math.abs((int) Math.round(center.distanceSquared(point)) - radius*radius) <= radius) {
						if (lookingat == onlyreplace) {
							world.getBlockAt(point).setType(barriertype);
						}  else {
							// nope
						}
					}
				}
			}
		}
	}

}
