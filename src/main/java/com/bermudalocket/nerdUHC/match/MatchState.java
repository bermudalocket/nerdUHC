package com.bermudalocket.nerdUHC.match;

public enum MatchState {

	PREGAME(false),
	STARTING(true),
	INPROGRESS(true),
	DEATHMATCH(true),
	END(false);

	private boolean _inProgress;

	MatchState(boolean inProgress) {
		_inProgress = inProgress;
	}

	public boolean isInProgress() {
		return _inProgress;
	}
	
}
