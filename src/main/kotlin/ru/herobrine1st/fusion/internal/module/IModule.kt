package ru.herobrine1st.fusion.internal.module

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction

interface IModule {
    fun registerCommands(commandListUpdateAction: CommandListUpdateAction)
    fun registerListener(jda: JDA)
}