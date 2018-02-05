package com.bermudalocket.nerdUHC.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.bermudalocket.nerdUHC.NerdUHC;

public class UHCUtils {

	private NerdUHC plugin;
	private World world;

	public UHCUtils(NerdUHC plugin, UHCMatch match) {
		this.plugin = plugin;
		this.world = match.getWorld();
		drawBarrier(true);
	}

	public void drawBarrier(boolean draw) {

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

		if (radius <= 0)
			return;

		for (int i = -radius - 1; i < radius + 1; i++) {
			for (int j = -radius - 1; j < radius + 1; j++) {
				for (int k = -radius - 1; k < radius + 1; k++) {
					point.setX(x + i);
					point.setY(y + j);
					point.setZ(z + k);
					Material lookingat = world.getBlockAt(point).getType();
					if (Math.abs((int) Math.round(center.distanceSquared(point)) - radius * radius) <= radius) {
						if (lookingat == onlyreplace) {
							world.getBlockAt(point).setType(replacewith);
						}
					}
				}
			}
		}
	}

	// Custom spreadplayers method
	/*
	@SuppressWarnings("deprecation")
	public void spreadPlayers(int nodes) {
		
		int cx = plugin.CONFIG.SPAWN_X;
		int cz = plugin.CONFIG.SPAWN_Z;
		int radius = (int) (world.getWorldBorder().getSize() / 2);
		int angle = (int) ((2 * Math.PI) / nodes);
		int n = match.getScoreboard().getTeams().size();
		double rand = 0;
		Location node = new Location(world, 0, 0, 0);
		
		ArrayList<OfflinePlayer> roster = new ArrayList<OfflinePlayer>();
		roster.addAll(match.getRoster());
		
		plugin.getLogger().info(roster.toString());
		
		HashMap<Team, Location> teamsmap = new HashMap<Team, Location>();
		
		int i = 0;
		for (OfflinePlayer op : roster) {
			if (!op.isOnline()) continue;
			
			Team t = match.getScoreboard().getPlayerTeam(op);
			if (teamsmap.containsKey(t)) {
				Location teamlocation = teamsmap.get(t);
				Player p = Bukkit.getPlayer(op.getUniqueId());
				p.teleport(teamlocation);
				continue;
			}
			
			rand = Math.random();
			node.setX(cx + rand * (radius * Math.cos(i * angle)));
			node.setZ(cz + (rand*400)/n + (radius * Math.sin(i * angle)));

			boolean unsafe = true;
			while (unsafe) {
				node.setY(world.getHighestBlockYAt(node.getX(), node.getY()));
				unsafe = (omitMaterials.contains(world.getBlockAt(node).getType()) || omitBiomes.contains(world.getBiome(x, z)));
				if (unsafe) x--;
			}
			
			teamsmap.put(t, node);
			Bukkit.getPlayer(op.getUniqueId()).teleport(node);
			i++;
		}
	}
	*/
}
