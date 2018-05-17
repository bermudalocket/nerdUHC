package com.bermudalocket.nerdUHC.util;

import com.bermudalocket.nerdUHC.match.Match;
import org.bukkit.WorldBorder;

public class UHCWorldBorder {

    private final WorldBorder worldBorder;

    private boolean isShrinking = false;
    private int shrinkRate = 0;

    public UHCWorldBorder(Match match) {
        this.worldBorder = match.getWorld().getWorldBorder();
        worldBorder.setSize(3500);
    }

    public int getSize() {
        return (int) worldBorder.getSize();
    }

    public int getShrinkRate() {
        return shrinkRate;
    }

    public void shrink() {
        shrinkRate = (int) Math.round((worldBorder.getSize() - 500) / (2 * 60 * 60));
        worldBorder.setSize(500, 60 * 60);
        worldBorder.setWarningDistance(60);
        worldBorder.setWarningTime(90);
        isShrinking = true;
    }

    public void freeze() {
        worldBorder.setSize(worldBorder.getSize());
        isShrinking = false;
    }

    public boolean isShrinking() {
        return isShrinking;
    }

}
