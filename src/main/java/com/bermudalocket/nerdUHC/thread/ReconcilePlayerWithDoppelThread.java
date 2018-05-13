package com.bermudalocket.nerdUHC.thread;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.match.Match;
import org.bukkit.entity.Player;

public class ReconcilePlayerWithDoppelThread extends AbstractThread {

    private Player _player;

    public ReconcilePlayerWithDoppelThread(Match abstractMatch, Player player) {
        super(abstractMatch);
        _player = player;
    }

    public void run() {
        NerdUHC.COMBAT_LOGGER.reconcileDoppelWithPlayer(_player);
    }

}
