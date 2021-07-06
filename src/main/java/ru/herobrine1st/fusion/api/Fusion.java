package ru.herobrine1st.fusion.api;

import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.manager.CommandManager;

import javax.inject.Inject;

public final class Fusion {
    private Fusion() {
    }

    @Inject
    private static CommandManager commandManager;

    @NotNull
    public static CommandManager getCommandManager() {
        return commandManager;
    }
}
