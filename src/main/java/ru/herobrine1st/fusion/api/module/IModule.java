package ru.herobrine1st.fusion.api.module;

import net.dv8tion.jda.api.JDA;
import ru.herobrine1st.fusion.api.manager.CommandManager;

public interface IModule {
    void registerCommands(CommandManager commandManager);
    void registerListener(JDA jda);
}