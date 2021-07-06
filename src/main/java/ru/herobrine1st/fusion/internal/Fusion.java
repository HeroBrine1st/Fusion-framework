package ru.herobrine1st.fusion.internal;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.event.FusionInitializationEvent;
import ru.herobrine1st.fusion.api.event.FusionPreInitializationEvent;
import ru.herobrine1st.fusion.api.event.FusionStartedEvent;
import ru.herobrine1st.fusion.api.manager.CommandManager;
import ru.herobrine1st.fusion.api.module.FutureModule;
import ru.herobrine1st.fusion.internal.command.SlashCommandBuilder;
import ru.herobrine1st.fusion.internal.listener.ButtonInteractionHandler;
import ru.herobrine1st.fusion.internal.listener.MessageCommandHandler;
import ru.herobrine1st.fusion.internal.listener.SlashCommandHandler;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;
import ru.herobrine1st.fusion.internal.manager.ExecutorServiceProvider;

import javax.security.auth.login.LoginException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class Fusion {
    private static final Logger logger = LoggerFactory.getLogger(Fusion.class);
    private final AnnotatedEventManager eventManager = new AnnotatedEventManager();
    private JdbcConnectionSource connectionSource = null;
    private void main() throws InterruptedException {
        logger.info("Starting Fusion Discord bot");
        logger.info("Connecting to database");
        try {
            connectionSource = new JdbcPooledConnectionSource(Config.INSTANCE.getDatabaseUrl());
        } catch (Exception throwable) {
            logger.error("An exception occurred while connecting to database",throwable);
            System.exit(-1);
            return;
        }
        logger.info("Loading modules");
        findModules();
        logger.info("Found %s modules".formatted(eventManager.getRegisteredListeners().size()));
        eventManager.handle(new FusionPreInitializationEvent());
        logger.info("Logging into discord...");
        JDA jda;
        try {
            jda = JDABuilder.createLight(Config.INSTANCE.getToken(), GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES)
                    .setEventManager(eventManager)
                    .addEventListeners(new MessageCommandHandler(), new SlashCommandHandler(), new ButtonInteractionHandler())
                    .build();
        } catch (LoginException e) {
            logger.error("Invalid discord token");
            System.exit(-1);
            return;
        }
        jda.awaitReady();
        logger.info("Logged in as %s".formatted(jda.getSelfUser().getAsTag()));
        eventManager.handle(new FusionInitializationEvent(jda));
        logger.info("Initializing slash command subsystem");
        logger.info("  Building commands into Discord data");
        Objects.requireNonNull(jda.getGuildById("394132321839874050")).updateCommands() // TODO concept
                .addCommands(CommandManagerImpl.INSTANCE.commands.stream()
                        .filter(SlashCommandBuilder::hasSlashSupport)
                        .map(SlashCommandBuilder::buildCommand)
                        .toList())
                .complete();
        logger.info("  Dispatched application commands");
        eventManager.handle(new FusionStartedEvent(jda));
        logger.info("Initialized Fusion Discord bot");
    }

    private void findModules() {
        Injector injector = Guice.createInjector(Stage.PRODUCTION, it -> {
            it.bind(CommandManager.class).toInstance(CommandManagerImpl.INSTANCE);
            it.bind(ExecutorService.class).toInstance(ExecutorServiceProvider.getExecutorService());
            it.bind(ConnectionSource.class).toInstance(connectionSource);
        });
        var disabledModules = Config.INSTANCE.getDisabledModules();
        new Reflections(Config.INSTANCE.getModuleSearchPrefix())
                .getTypesAnnotatedWith(FutureModule.class)
                .stream()
                .filter(it -> !disabledModules.contains(it.getAnnotation(FutureModule.class).id().toLowerCase()))
                .peek(it -> logger.trace("Loading module " + it.getAnnotation(FutureModule.class).id()))
                .forEach(it -> {
                    Object instance = null;
                    try {
                        Field instanceField = it.getDeclaredField("INSTANCE");
                        instanceField.setAccessible(true);
                        instance = Objects.requireNonNull(instanceField.get(null), "INSTANCE field is null");
                        injector.injectMembers(instance);
                    } catch (Exception instanceFieldException) {
                        try {
                            instance = injector.getInstance(it);
                        } catch (Exception instantiationException) {
                            logger.error("No instance found or created for module %s".formatted(it.getCanonicalName()));
                            logger.error("No static INSTANCE field", instanceFieldException);
                            logger.error("No empty constructor or occurred exception constructing instance", instantiationException);
                        }
                    }
                    if (instance != null)
                        eventManager.register(instance);
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
