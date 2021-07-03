package ru.herobrine1st.fusion;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.module.AbstractModule;
import ru.herobrine1st.fusion.api.module.FutureModule;
import ru.herobrine1st.fusion.internal.Config;
import ru.herobrine1st.fusion.internal.command.SlashCommandBuilder;
import ru.herobrine1st.fusion.internal.listener.ButtonInteractionHandler;
import ru.herobrine1st.fusion.internal.listener.MessageCommandHandler;
import ru.herobrine1st.fusion.internal.listener.SlashCommandHandler;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fusion {
    private static final Logger logger = LoggerFactory.getLogger(Fusion.class);
    private final List<AbstractModule> modules = new ArrayList<>();
    private JDA jda;

    private void main() throws InterruptedException {
        logger.info("Starting Fusion Discord bot");
        logger.info("Logging in...");
        try {
            jda = JDABuilder.createLight(Config.INSTANCE.getToken(), GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES)
                    .addEventListeners(new MessageCommandHandler(), new SlashCommandHandler(), new ButtonInteractionHandler())
                    .build();
        } catch (LoginException e) {
            logger.error("Invalid discord token");
            System.exit(-1);
            return;
        }
        logger.info("Loading modules");
        findModules();
        logger.info("Found %s modules".formatted(modules.size()));
        modules.forEach(it -> {
            try {
                if (logger.isTraceEnabled()) {
                    var moduleId = it.getClass().getAnnotation(FutureModule.class).id();
                    logger.trace("Initializing module " + moduleId);
                }
                it.registerCommands(CommandManagerImpl.INSTANCE);
                it.registerListener(jda);
            } catch (Exception e) {
                logger.error("Module initialization error", e);
            }
        });
        jda.awaitReady();
        logger.info("Logged in as %s".formatted(jda.getSelfUser().getAsTag()));
        logger.info("Initializing slash command subsystem");
        logger.info("Building commands into Discord data");
        Objects.requireNonNull(jda.getGuildById("394132321839874050")).updateCommands() // TODO concept
                .addCommands(CommandManagerImpl.INSTANCE.commands.stream()
                        .filter(SlashCommandBuilder::hasSlashSupport)
                        .map(SlashCommandBuilder::buildCommand)
                        .toList())
                .complete();
        logger.info("Dispatched list of commands");

        logger.warn("No database is connected"); // TODO
        logger.info("Initialized Fusion Discord bot");
        //if(jda.getStatus() == JDA.Status.SHUTDOWN || jda.getStatus() == JDA.Status.SHUTTING_DOWN)
    }

    private void findModules() {
        var disabledModules = Config.INSTANCE.getDisabledModules();
        new Reflections(Config.INSTANCE.getModuleSearchPrefix())
                .getTypesAnnotatedWith(FutureModule.class)
                .stream()
                .filter(AbstractModule.class::isAssignableFrom)
                .filter(it -> !disabledModules.contains(it.getAnnotation(FutureModule.class).id().toLowerCase()))
                .<Class<? extends AbstractModule>>map(it -> it.asSubclass(AbstractModule.class))
                .peek(it -> logger.trace("Loading module " + it.getAnnotation(FutureModule.class).id()))
                .forEach(it -> {
                    try {
                        this.modules.add(it.getDeclaredConstructor().newInstance());
                    } catch (Exception e) {
                        logger.error("Module instantiation error", e); // По идее быть не должно, ибо там есть дефолтный конструктор
                    }
                });
    }


    /////////////////
    public static final Fusion INSTANCE = new Fusion();

    private Fusion() {
    }

    public static void main(String[] args) throws InterruptedException {
        INSTANCE.main();
    }
}
