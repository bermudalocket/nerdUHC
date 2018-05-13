package com.bermudalocket.nerdUHC.thread;

import com.bermudalocket.nerdUHC.match.Match;

import static com.bermudalocket.nerdUHC.NerdUHC.THREAD_HANDLER;

abstract class AbstractThread implements Runnable {

    private int _taskId;

    Match _match;

    AbstractThread(Match abstractMatch) {
        _match = abstractMatch;
    }

    int getTaskId() {
        return _taskId;
    }

    void setTaskId(int taskId) {
        _taskId = taskId;
    }

    void cancel() {
        THREAD_HANDLER.removeThread(this);
    }

    public abstract void run();

}
