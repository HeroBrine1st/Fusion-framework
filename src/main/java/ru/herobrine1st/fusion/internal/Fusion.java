package ru.herobrine1st.fusion.internal;

import com.google.inject.Guice;
import com.google.inject.Stage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.annotation.FusionModule;
import ru.herobrine1st.fusion.api.command.build.FusionCommandData;
import ru.herobrine1st.fusion.api.event.FusionInitializationEvent;
import ru.herobrine1st.fusion.api.event.FusionPreInitializationEvent;
import ru.herobrine1st.fusion.api.event.FusionStartedEvent;
import ru.herobrine1st.fusion.api.manager.CommandManager;
import ru.herobrine1st.fusion.internal.command.SlashCommandBuilder;
import ru.herobrine1st.fusion.internal.listener.ButtonInteractionHandler;
import ru.herobrine1st.fusion.internal.listener.MessageCommandHandler;
import ru.herobrine1st.fusion.internal.listener.SlashCommandHandler;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;
import ru.herobrine1st.fusion.internal.manager.ExecutorServiceProvider;

import javax.security.auth.login.LoginException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ru.herobrine1st.fusion.api.Fusion.Internal;

public class Fusion implements Internal {
    private static final Logger logger = LoggerFactory.getLogger(Fusion.class);
    private final AnnotatedEventManager eventManager = new AnnotatedEventManager();
    private Connection connection;

    private void main() throws InterruptedException {
        logger.info("Starting Fusion Discord bot");
        logger.info("Connecting to database");
        try {
            connection = DriverManager.getConnection(Config.INSTANCE.getDatabaseUrl());
        } catch (SQLException exception) {
            logger.error("An error occurred while connecting to database", exception);
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
                    .addEventListeners(this, new MessageCommandHandler(), new SlashCommandHandler(), new ButtonInteractionHandler())
                    .build();
        } catch (LoginException e) {
            logger.error("Invalid discord token");
            System.exit(-1);
            return;
        }
        logger.info("Initializing static Fusion");
        Guice.createInjector(Stage.PRODUCTION, it -> {
            it.bind(Internal.class).toInstance(INSTANCE);
            it.requestStaticInjection(ru.herobrine1st.fusion.api.Fusion.class);
        });
        jda.awaitReady();
        logger.info("Logged in as %s".formatted(jda.getSelfUser().getAsTag()));
        eventManager.handle(new FusionInitializationEvent(jda));
        logger.info("Initializing slash commands");
        initializeSlashCommands(jda);
        eventManager.handle(new FusionStartedEvent(jda));
        System.out.println();
        logger.info("Fusion bot started!");
        logger.info("Loaded %s commands".formatted(CommandManagerImpl.INSTANCE.commands.size()));
        logger.info("Loaded %s modules".formatted(eventManager.getRegisteredListeners().size() - 4));
        logger.info("Serving %s guilds".formatted(jda.getGuildCache().size()));
        System.out.println();
        getExecutorService().scheduleAtFixedRate(() -> {
            try {                                // Выглядит как ебаный говнокод
                if (!connection.isValid(1000)) { // Потому что вот эта хуйня кидает SQLException вместо IllegalArgumentException
                    logger.error("Disconnected from database. Reconnecting..");
                    try {
                        connection = DriverManager.getConnection(Config.INSTANCE.getDatabaseUrl());
                        logger.info("Successfully reconnected to database");
                    } catch (SQLException exception) {
                        logger.error("An error occurred while connecting to database", exception);
                        jda.shutdownNow();
                    }

                }
            } catch (SQLException ignored) {}
        }, 0, 30, TimeUnit.SECONDS);
    }

    @SubscribeEvent
    public void onShutdown(ShutdownEvent event) {
        logger.info("Shutting down Fusion bot");
        getExecutorService().shutdown();
    }

    private void initializeSlashCommands(JDA jda) {
        List<FusionCommandData<?>> commands = CommandManagerImpl.INSTANCE.commands.stream()
                .filter(SlashCommandBuilder::hasSlashSupport)
                .toList();
        String testGuildId = Config.INSTANCE.getTestGuildId();
        if (testGuildId != null) {
            Guild testGuild = jda.getGuildById(testGuildId);
            if (testGuild != null)
                testGuild.updateCommands()
                    .addCommands(commands.stream()
                            .filter(FusionCommandData::isTesting)
                            .peek(it -> logger.debug("Registering command %s in testing context".formatted(it.getName())))
                            .map(SlashCommandBuilder::buildCommand)
                            .toList())
                    .complete();
            else logger.warn("Could not obtain guild with id %s".formatted(testGuildId));
        } else if (logger.isDebugEnabled())
            logger.warn("No TEST_GUILD_ID provided - skipping commands marked as testing");
        jda.updateCommands()
                .addCommands(commands.stream()
                        .filter(it -> !it.isTesting())
                        .peek(it -> logger.debug("Registering command %s in production context".formatted(it.getName())))
                        .map(SlashCommandBuilder::buildCommand)
                        .toList())
                .complete();
    }

    private void findModules() {
        var disabledModules = Config.INSTANCE.getDisabledModules();
        new Reflections(Config.INSTANCE.getModuleSearchPrefix())
                .getTypesAnnotatedWith(FusionModule.class)
                .stream()
                .filter(it -> !disabledModules.contains(it.getAnnotation(FusionModule.class).id().toLowerCase()))
                .peek(it -> logger.trace("Loading module " + it.getAnnotation(FusionModule.class).id()))
                .forEach(it -> {
                    Object instance = null;
                    try {
                        Field instanceField = it.getDeclaredField("INSTANCE");
                        instanceField.setAccessible(true);
                        instance = Objects.requireNonNull(instanceField.get(null), "INSTANCE field is null");
                    } catch (Exception instanceFieldException) {
                        try {
                            instance = it.getDeclaredConstructor().newInstance();
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

    //region INSTANCE and main method
    public static final Fusion INSTANCE = new Fusion();

    private Fusion() {
    }

    public static void main(String[] args) throws InterruptedException {
        INSTANCE.main();
    }
    //endregion

    @Override
    public Connection getSqlConnection() {
        return connection;
    }

    @Override
    public CommandManager getCommandManager() {
        return CommandManagerImpl.INSTANCE;
    }

    @Override
    public ScheduledExecutorService getExecutorService() {
        return ExecutorServiceProvider.getExecutorService();
    }

    @Override
    public IEventManager getEventManager() {
        return eventManager;
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("1223");
    }
}
