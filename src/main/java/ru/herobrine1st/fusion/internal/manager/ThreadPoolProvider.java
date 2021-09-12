package ru.herobrine1st.fusion.internal.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;

public final class ThreadPoolProvider {
    private ThreadPoolProvider() {}

    private static final ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(2, it -> {
        var t = new Thread(it);
        t.setDaemon(true);
        return t;
    });

    private static final ForkJoinPool connectionPool = new ForkJoinPool();

    public static ScheduledExecutorService getScheduledPool() {
        return scheduledPool;
    }

    public static ForkJoinPool getConnectionPool() {
        return connectionPool;
    }
}
