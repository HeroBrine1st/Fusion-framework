package ru.herobrine1st.fusion.internal.manager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.option.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.option.FusionCommand;
import ru.herobrine1st.fusion.api.exception.ExceptionHandler;
import ru.herobrine1st.fusion.api.manager.CommandManager;
import ru.herobrine1st.fusion.internal.command.SlashCommandBuilder;
import ru.herobrine1st.fusion.internal.listener.ButtonInteractionHandler;
import ru.herobrine1st.fusion.internal.listener.SlashCommandHandler;

import java.util.ArrayList;
import java.util.List;

public class CommandManagerImpl implements CommandManager {
    public final static Logger logger = LoggerFactory.getLogger(CommandManagerImpl.class);
    public final List<FusionCommand<?>> commands = new ArrayList<>();
    private final JDA jda;
    private ExceptionHandler exceptionHandler;

    public CommandManagerImpl(JDA jda) {
        this.jda = jda;
        exceptionHandler = (ctx, exception) -> ctx.getEvent().reply(exception.getMessage()).setEphemeral(true).queue();
    }

    @Override
    public void registerCommand(FusionCommand<?> data) {
        if (commands.stream().map(FusionBaseCommand::getName).anyMatch(it -> it.equals(data.getName()))) {
            throw new RuntimeException("Intersecting name: " + data.getName());
        }
        commands.add(data);
    }

    @Override
    public List<FusionCommand<?>> getCommands() {
        return commands;
    }

    @Override
    public void updateCommands(Guild testingGuild) {
        if (testingGuild != null) {
            testingGuild.updateCommands()
                    .addCommands(commands.stream()
                            .filter(FusionCommand::isTesting)
                            .peek(it -> logger.debug("Registering command %s in testing context".formatted(it.getName())))
                            .map(SlashCommandBuilder::buildCommand)
                            .toList())
                    .queue(null, throwable -> logger.error("Could not send slash commands", throwable));
        } else if (logger.isDebugEnabled())
            logger.warn("No testingGuild provided - skipping commands marked as testing");
        jda.updateCommands()
                .addCommands(commands.stream()
                        .filter(it -> !it.isTesting())
                        .peek(it -> logger.debug("Registering command %s in production context".formatted(it.getName())))
                        .map(SlashCommandBuilder::buildCommand)
                        .toList())
                .queue(null, throwable -> logger.error("Could not send slash commands", throwable));
    }

    @Override
    public void registerListeners() {
        jda.addEventListener(ButtonInteractionHandler.INSTANCE, new SlashCommandHandler(this));
    }

    @Override
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void handleException(CommandContext ctx, Exception exception) {
        exceptionHandler.handle(ctx, exception);
    }
}