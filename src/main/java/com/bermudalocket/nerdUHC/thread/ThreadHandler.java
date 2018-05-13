package com.bermudalocket.nerdUHC.thread;

import com.bermudalocket.nerdUHC.Configuration;
import com.bermudalocket.nerdUHC.NerdUHC;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashSet;

public class ThreadHandler {

    private static final BukkitScheduler SCHEDULER = NerdUHC.PLUGIN.getServer().getScheduler();

    private final HashSet<AbstractThread> _threads = new HashSet<>();

    public void schedule(long delayInTicks, Runnable runnable) {
        int taskId = SCHEDULER.scheduleSyncDelayedTask(NerdUHC.PLUGIN, runnable, delayInTicks);
        ((AbstractThread) runnable).setTaskId(taskId);
        _threads.add((AbstractThread) runnable);
        if (Configuration.DO_DEBUG) {
            NerdUHC.PLUGIN.getLogger().info("THREAD HANDLER: scheduled new thread ("
                    + runnable.getClass().getSimpleName() + ") with delay " + delayInTicks + " ticks");
        }
    }

    public void scheduleRepeating(long delayInTicks, long periodInTicks, Runnable runnable) {
        int taskId = SCHEDULER.scheduleSyncRepeatingTask(NerdUHC.PLUGIN, runnable, delayInTicks, periodInTicks);
        ((AbstractThread) runnable).setTaskId(taskId);
        _threads.add((AbstractThread) runnable);
        if (Configuration.DO_DEBUG) {
            NerdUHC.PLUGIN.getLogger().info("THREAD HANDLER: scheduled new repeating thread ("
                    + runnable.getClass().getSimpleName() + ") with period " + periodInTicks + " ticks, and delay "
                    + delayInTicks + " ticks");
        }
    }

    public void removeThread(Class clazz) {
        for (AbstractThread abstractThread : _threads) {
            if (abstractThread.getClass().equals(clazz)) {
                removeThread(abstractThread);
            }
        }
    }

    void removeThread(AbstractThread abstractThread) {
        _threads.remove(abstractThread);
        SCHEDULER.cancelTask(abstractThread.getTaskId());
        if (Configuration.DO_DEBUG) {
            NerdUHC.PLUGIN.getLogger().info("THREAD HANDLER: removed thread " + abstractThread);
        }
    }

    public void removeAll() {
        SCHEDULER.cancelAllTasks();
        if (Configuration.DO_DEBUG) {
            NerdUHC.PLUGIN.getLogger().info("THREAD HANDLER: removed all threads");
        }
    }

    public boolean threadExists(Class clazz) {
        return _threads.stream().anyMatch(t -> t.getClass().equals(clazz));
    }

}
