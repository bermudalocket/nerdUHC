package com.bermudalocket.nerdUHC.match;

public class MatchHandler implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Match _match;

	public MatchHandler() {
		getNewMatch();
	}
	
	public void getNewMatch() {
		_match = new Match();
	}
	
	public Match getMatch() {
		return _match;
	}

	public void load(MatchHandler matchHandler) {
		_match = matchHandler.getMatch();
	}
	
}
