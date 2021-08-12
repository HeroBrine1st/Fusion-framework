package ru.herobrine1st.fusion.api;

import net.dv8tion.jda.api.hooks.IEventManager;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.manager.CommandManager;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;

public final class Fusion {
    public interface Internal {
        Connection getSqlConnection();
        CommandManager getCommandManager();
        ScheduledExecutorService getExecutorService();
        IEventManager getEventManager();
        ResourceBundle getResourceBundle();
    }

    private Fusion() {
    }

    @Inject
    private static Fusion.Internal internalFusion;

    @NotNull
    public static Connection getSqlConnection() {
        return internalFusion.getSqlConnection();
    }

    @NotNull
    public static CommandManager getCommandManager() {
        return internalFusion.getCommandManager();
    }

    @NotNull
    public static Fusion.Internal getInternalFusion() {
        return internalFusion;
    }

    @NotNull
    public static ScheduledExecutorService getExecutorService() {
        return internalFusion.getExecutorService();
    }

    @NotNull
    public static ResourceBundle getResourceBundle() {
        return internalFusion.getResourceBundle();
    }
}
