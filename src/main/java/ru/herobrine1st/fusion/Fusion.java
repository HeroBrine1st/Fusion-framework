package ru.herobrine1st.fusion;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.module.FutureModule;
import ru.herobrine1st.fusion.api.module.AbstractModule;
import ru.herobrine1st.fusion.internal.Config;
import ru.herobrine1st.fusion.internal.command.SlashCommandBuilder;
import ru.herobrine1st.fusion.internal.listener.MessageCommandHandler;
import ru.herobrine1st.fusion.internal.listener.SlashCommandHandler;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;

import javax.security.auth.login.LoginException;
import java.util.*;

public class Fusion {
    private static final Logger logger = LoggerFactory.getLogger(Fusion.class);
    private final List<AbstractModule> modules = new ArrayList<>();
    private JDA jda;
    private void main() throws InterruptedException {

        logger.info("Starting Fusion Discord bot");
        logger.info("Logging in...");
        try {
            jda = JDABuilder.createLight(Config.INSTANCE.getToken(), EnumSet.noneOf(GatewayIntent.class))
                    .addEventListeners(new MessageCommandHandler(), new SlashCommandHandler())
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
                if(logger.isTraceEnabled()) {
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
    }

    private void findModules() {
        var disabledModules = Config.INSTANCE.getDisabledModules();
        new Reflections(Config.INSTANCE.getModuleSearchPrefix())
                .getTypesAnnotatedWith(FutureModule.class)
                .stream()
                .filter(it -> {
                    var result = AbstractModule.class.isAssignableFrom(it);
                    if(logger.isTraceEnabled() && !result) {
                        logger.trace("Module %s is not subclass of AbstractModule".formatted(it.getAnnotation(FutureModule.class).id()));
                    }
                    return result;
                })
                .filter(it -> {
                    var moduleId = it.getAnnotation(FutureModule.class).id();
                    return disabledModules.stream().noneMatch(moduleId::equals);
                })
                .<Class<? extends AbstractModule>>map(it -> it.asSubclass(AbstractModule.class))
                .forEach(it -> { // Потому что нельзя совместить map и filter
                    if (logger.isTraceEnabled()) {
                        var moduleId = it.getAnnotation(FutureModule.class).id();
                        logger.trace("Loading module " + moduleId);
                    }
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
