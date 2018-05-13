package com.bermudalocket.nerdUHC.thread;

import com.bermudalocket.nerdUHC.match.Match;
import com.bermudalocket.nerdUHC.util.MatchState;
import org.bukkit.entity.Player;

import static com.bermudalocket.nerdUHC.NerdUHC.COMBAT_LOGGER;
import static com.bermudalocket.nerdUHC.NerdUHC.GUI_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.PLAYER_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.SCOREBOARD_HANDLER;

public class PlayerJoinThread extends AbstractThread {

    private Player _player;

    public PlayerJoinThread(Match abstractMatch, Player player) {
        super(abstractMatch);
        _player = player;
    }

    public void run() {
        SCOREBOARD_HANDLER.setPlayerBoard(_player);
        SCOREBOARD_HANDLER.refresh();
        COMBAT_LOGGER.reconcileDoppelWithPlayer(_player);
        PLAYER_HANDLER.formatDisplayName(_player);

        if (!SCOREBOARD_HANDLER.hasTeam(_player)) {
            _player.teleport(_match.getWorld().getSpawnLocation());
            if (_match.inState(MatchState.PREGAME)) {
                PLAYER_HANDLER.resetPlayer(_player);
                GUI_HANDLER.givePlayerGUIItems(_player);
            } else {
                PLAYER_HANDLER.makeSpectator(_player);
            }
        }
    }

}
