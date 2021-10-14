package ru.herobrine1st.fusion.internal;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.build.FusionCommand;
import ru.herobrine1st.fusion.api.event.FusionInitializationEvent;
import ru.herobrine1st.fusion.api.event.FusionPreInitializationEvent;
import ru.herobrine1st.fusion.api.event.FusionStartedEvent;
import ru.herobrine1st.fusion.api.manager.CommandManager;
import ru.herobrine1st.fusion.internal.command.SlashCommandBuilder;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;

import java.lang.reflect.Field;
import java.util.List;

import static ru.herobrine1st.fusion.api.Fusion.Internal;

public class Fusion implements Internal {
    private static final Logger logger = LoggerFactory.getLogger(Fusion.class);
    private final AnnotatedEventManager eventManager = new AnnotatedEventManager();
    private Config config = new Config();
    public static final Fusion INSTANCE = new Fusion();

    static {
        Field field;
        try {
            field = ru.herobrine1st.fusion.api.Fusion.class.getDeclaredField("internalFusion");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        try {
            field.set(null, INSTANCE);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void start(JDA jda) throws InterruptedException {
        logger.info("Starting Fusion Discord bot");
        eventManager.handle(new FusionPreInitializationEvent(jda));
        jda.awaitReady();
        logger.info("Logged in as %s".formatted(jda.getSelfUser().getAsTag()));
        eventManager.handle(new FusionInitializationEvent(jda));
        logger.info("Initializing slash commands");
        initializeSlashCommands(jda);
        eventManager.handle(new FusionStartedEvent(jda));
    }

    @SubscribeEvent
    public void onShutdown(ShutdownEvent event) {
        logger.info("Shutting down Fusion bot");
    }

    private void initializeSlashCommands(JDA jda) {
        List<FusionCommand<?>> commands = CommandManagerImpl.INSTANCE.commands.stream()
                .filter(SlashCommandBuilder::hasSlashSupport)
                .toList();
        String testGuildId = config.getTestGuildId();
        if (testGuildId != null) {
            Guild testGuild = jda.getGuildById(testGuildId);
            if (testGuild != null)
                testGuild.updateCommands()
                    .addCommands(commands.stream()
                            .filter(FusionCommand::isTesting)
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

    public Config getConfig() {
        return config;
    }

    @Override
    public CommandManager getCommandManager() {
        return CommandManagerImpl.INSTANCE;
    }

    @Override
    public IEventManager getEventManager() {
        return eventManager;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
