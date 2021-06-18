package ru.herobrine1st.fusion.api.module;

import net.dv8tion.jda.api.JDA;
import ru.herobrine1st.fusion.api.manager.CommandManager;

public abstract class AbstractModule {
    public abstract void registerCommands(CommandManager commandManager);

    public abstract void registerListener(JDA jda);

    public AbstractModule() {
    }
}