package com.bermudalocket.nerdUHC.match;

public class MatchHandler {

    private Match match;

    public MatchHandler() {
        getNewMatch();
    }

    public void getNewMatch() {
        match = new Match();
    }

    public Match getMatch() {
        return match;
    }

}
