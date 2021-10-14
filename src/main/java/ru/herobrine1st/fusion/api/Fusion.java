package ru.herobrine1st.fusion.api;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.IEventManager;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.manager.CommandManager;

public final class Fusion {
    public interface Internal {
        CommandManager getCommandManager();

        IEventManager getEventManager();

        void start(JDA jda) throws InterruptedException;
    }

    private Fusion() {}

    /**
     * To be injected
     */
    @SuppressWarnings("unused")
    private static Fusion.Internal internalFusion;

    @NotNull
    public static CommandManager getCommandManager() {
        return internalFusion.getCommandManager();
    }

    @NotNull
    public static Fusion.Internal getInternalFusion() {
        return internalFusion;
    }

    public static void start(JDA jda) throws InterruptedException {
        internalFusion.start(jda);
    }
}
