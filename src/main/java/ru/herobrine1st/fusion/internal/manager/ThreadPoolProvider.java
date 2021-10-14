package ru.herobrine1st.fusion.internal.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class ThreadPoolProvider {
    private ThreadPoolProvider() {}

    private static final ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1, it -> {
        var t = new Thread(it);
        t.setDaemon(true);
        return t;
    });

    private static final ExecutorService connectionPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static ScheduledExecutorService getScheduledPool() {
        return scheduledPool;
    }

    public static ExecutorService getConnectionPool() {
        return connectionPool;
    }
}
