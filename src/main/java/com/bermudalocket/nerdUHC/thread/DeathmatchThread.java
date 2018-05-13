package com.bermudalocket.nerdUHC.thread;

import com.bermudalocket.nerdUHC.match.Match;
import org.bukkit.ChatColor;

import static com.bermudalocket.nerdUHC.NerdUHC.SCOREBOARD_HANDLER;

public class DeathmatchThread extends AbstractThread {

    public DeathmatchThread(Match abstractMatch) {
        super(abstractMatch);
    }

    public void run() {
        _match.beginDeathmatch();
        SCOREBOARD_HANDLER.updateTitle(ChatColor.RED + "" + ChatColor.BOLD + "Deathmatch!");
    }

}
