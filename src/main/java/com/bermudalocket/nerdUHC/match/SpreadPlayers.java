package com.bermudalocket.nerdUHC.match;

import java.util.ArrayList;

import org.bukkit.util.Vector;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.modules.UHCMatch;

public class SpreadPlayers extends UHCMatch {
	
	// Custom spread players method
	
	public SpreadPlayers(NerdUHC plugin, UHCMatch previousmatch) {
		super(plugin, previousmatch);
	}

	public ArrayList<Vector> layNodes(int nodes, int radius, Vector center) {
		
		ArrayList<Vector> vectors = new ArrayList<Vector>();
		int x = radius;
		int y = 65;
		int z = 0;
				
		// Split up the inner angle (2pi rad, aka 360 deg) into equal pieces
		double angle = (2 * Math.PI) / nodes;
		
		vectors.add(new Vector(x,y,z));
		for (int i = 1; i < nodes; i++) {
			double nextX = Math.cos(i*angle);
			double nextZ = Math.sin(i*angle + Math.asin(y/radius));
			vectors.add(new Vector(nextX, y, nextZ));
		}
		
		return vectors;
	}

}
