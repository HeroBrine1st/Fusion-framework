package ru.herobrine1st.fusion.internal.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ExecutorServiceProvider {
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2, it -> {
        var t = new Thread(it);
        t.setDaemon(true);
        return t;
    });

    public static ScheduledExecutorService getExecutorService() {
        return executorService;
    }
}
