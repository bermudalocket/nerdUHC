package com.bermudalocket.nerdUHC.thread;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.bermudalocket.nerdUHC.NerdUHC.MATCH_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.PLAYER_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.SCOREBOARD_HANDLER;
import static com.bermudalocket.nerdUHC.NerdUHC.THREAD_HANDLER;

public class RecycleMatchThread extends AbstractThread {

    public RecycleMatchThread() {
        super(null);
    }

    public void run() {
        THREAD_HANDLER.removeAll();
        MATCH_HANDLER.getNewMatch();

        Bukkit.getOnlinePlayers().forEach(player -> {
            PLAYER_HANDLER.resetPlayer(player);
            SCOREBOARD_HANDLER.setPlayerBoard(player);
            Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, null));
        });
        SCOREBOARD_HANDLER.refresh();
    }

}
