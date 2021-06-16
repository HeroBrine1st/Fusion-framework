package ru.herobrine1st.fusion.internal.module.test

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction
import ru.herobrine1st.fusion.internal.module.IModule
import java.util.concurrent.TimeUnit


object TestModule : IModule, ListenerAdapter() {
    override fun registerCommands(commandListUpdateAction: CommandListUpdateAction) {
        commandListUpdateAction.addCommands(
            CommandData("say", "Makes the bot say what you tell it to")
                .addOptions(
                    OptionData(OptionType.STRING, "content", "What the bot should say", true),
                    OptionData(OptionType.BOOLEAN, "ephemeral", "Should message be ephemeral?", false)
                ),
            CommandData("childtest", "Тестируем дочерние команды")
                .addSubcommands(
                    SubcommandData("child", "Дочерняя команда"),
                    SubcommandData("child2", "Дочерняя команда")
                )
        )
    }

    override fun registerListener(jda: JDA) {
        jda.addEventListener(this)
    }

    override fun onSlashCommand(event: SlashCommandEvent) {
        when (event.name) {
            "say" -> {
                // event.reply(event.getOption("content")!!.asString).queue()
                event.deferReply(event.getOption("ephemeral")?.asBoolean ?: false).delay(1, TimeUnit.SECONDS).flatMap {
                    // it.sendMessage(event.getOption("content")!!.asString)
                    it.sendMessageEmbeds(
                        EmbedBuilder()
                            .setTitle("Say")
                            .setDescription(event.getOption("content")!!.asString)
                            .build()
                    )
                }.queue()
            }
            "childtest" -> when (event.subcommandName) {
                "child" -> event.reply("Проверка дочерней ебаной команды завершена").queue()
                "child2" -> event.reply("Проверка дочерней ебаной команды2 завершена").queue()
            }
        }
    }
}