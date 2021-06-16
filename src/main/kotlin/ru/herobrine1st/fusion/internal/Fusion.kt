package ru.herobrine1st.fusion.internal

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.herobrine1st.fusion.internal.module.test.TestModule
import java.util.*
import kotlin.system.exitProcess


val logger: Logger = LoggerFactory.getLogger("Fusion")

fun main(args: Array<String>) {
    if(args.isEmpty()) {
        logger.error("No access token provided")
        exitProcess(-1)
    }
    val jda = JDABuilder.createLight(args[0], EnumSet.noneOf(GatewayIntent::class.java)) // slash commands don't need any intents
        //.addEventListeners()
        .build()
    jda.awaitReady()
    val commands: CommandListUpdateAction = jda.getGuildById(394132321839874050L)!!.updateCommands()
    // = jda.updateCommands()

    TestModule.registerCommands(commands)
    TestModule.registerListener(jda)

    commands.queue()
}