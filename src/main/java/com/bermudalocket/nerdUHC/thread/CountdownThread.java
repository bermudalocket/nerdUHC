package com.bermudalocket.nerdUHC.thread;

import com.bermudalocket.nerdUHC.NerdUHC;
import com.bermudalocket.nerdUHC.match.Match;
import com.bermudalocket.nerdUHC.util.UHCSounds;
import org.bukkit.ChatColor;

public class CountdownThread extends AbstractThread {

    private int _countFrom;

    public CountdownThread(Match abstractMatch, int countFrom) {
        super(abstractMatch);
        _countFrom = countFrom;
    }

    @Override
    public void run() {
        if (_countFrom == 0) {
            _match.beginMatch();
            cancel();
        }

        NerdUHC.PLUGIN.getServer().getOnlinePlayers().forEach(p ->
            p.sendTitle(ChatColor.RED + "The UHC starts in", _countFrom + " seconds", 10, 20, 10)
        );
        UHCSounds.TIMERTICK.playSound();
        _countFrom--;
    }

}
