package com.bermudalocket.nerdUHC.modules;

import org.bukkit.WorldBorder;

public class UHCWorldBorder {

	private final WorldBorder worldBorder;

	private boolean isShrinking = false;

	UHCWorldBorder(UHCMatch match) {
		this.worldBorder = match.getWorld().getWorldBorder();
	}

	public void shrink() {
		worldBorder.setSize(500, 60*60);
		worldBorder.setWarningDistance(60);
		worldBorder.setWarningTime(90);
		isShrinking = true;
		UHCLibrary.LIB_BORDER_SHRINKING.broadcast();
	}

	public void freeze() {
		worldBorder.setSize(worldBorder.getSize());
		isShrinking = false;
		UHCLibrary.LIB_BORDER_FROZEN.broadcast();
	}

	public boolean isShrinking() {
		return isShrinking;
	}

}
