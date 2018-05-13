package com.bermudalocket.nerdUHC.thread;

import com.bermudalocket.nerdUHC.match.Match;
import com.bermudalocket.nerdUHC.util.UHCLibrary;
import com.bermudalocket.nerdUHC.util.UHCSounds;

public class PvpThread extends AbstractThread {

    public PvpThread(Match abstractMatch) {
        super(abstractMatch);
    }

    public void run() {
        _match.getWorld().setPVP(true);

        UHCLibrary.LIB_PVP_ENABLED.sendAsTitle();
        UHCSounds.PVP_ENABLED.playSound();
    }

}
