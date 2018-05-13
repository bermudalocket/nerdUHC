package com.bermudalocket.nerdUHC.thread;

import com.bermudalocket.nerdUHC.match.Match;
import org.bukkit.ChatColor;

import java.util.concurrent.TimeUnit;

import static com.bermudalocket.nerdUHC.NerdUHC.SCOREBOARD_HANDLER;

public class ScoreboardThread extends AbstractThread {

    private long _duration;

    public ScoreboardThread(Match abstractMatch) {
        super(abstractMatch);
        _duration = _match.getDuration()*60;
    }

    @Override
    public void run() {

        if (_duration <= 0) {
            SCOREBOARD_HANDLER.refresh();
            return;
        }

        long h = TimeUnit.SECONDS.toHours(_duration);
        long m = TimeUnit.SECONDS.toMinutes(_duration) - 60 * h;
        long s = _duration - 60 * m - 60 * 60 * h;

        ChatColor color = (_duration < 5 * 60) ? ChatColor.RED : ChatColor.WHITE;
        String title = String.format("%s%s%02d:%02d:%02d", color, ChatColor.BOLD, h, m, s);

        SCOREBOARD_HANDLER.updateTitle(title);

        _duration--;
    }

}
