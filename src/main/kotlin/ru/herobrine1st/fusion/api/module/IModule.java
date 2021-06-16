package ru.herobrine1st.fusion.api.module;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public interface IModule {
    void registerCommands(CommandListUpdateAction commandListUpdateAction);
    void registerListener(JDA jda);
}