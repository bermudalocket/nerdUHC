package com.bermudalocket.nerdUHC.thread;

import com.bermudalocket.nerdUHC.match.Match;
import com.bermudalocket.nerdUHC.util.UHCLibrary;
import com.bermudalocket.nerdUHC.util.UHCSounds;
import org.bukkit.World;

public class WorldBorderThread extends AbstractThread {

    private int _minSize;
    private int _overDuration;

    public WorldBorderThread(Match abstractMatch, int minSize, int overDuration) {
        super(abstractMatch);
        _minSize = minSize;
        _overDuration = overDuration;
    }

    public void run() {
        World world = _match.getWorld();
        world.getWorldBorder().setSize(_minSize, _overDuration);

        UHCLibrary.LIB_BORDER_SHRINKING.sendAsTitle();
        UHCSounds.MATCHSTART.playSound();
    }

}
