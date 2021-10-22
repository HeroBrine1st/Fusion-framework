package ru.herobrine1st.fusion.internal.manager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.utils.Checks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.build.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.build.FusionCommand;
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

    public CommandManagerImpl(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void registerCommand(FusionCommand<?> data) {
        if (commands.stream().map(FusionBaseCommand::getName).anyMatch(it -> it.equals(data.getName()))) {
            throw new RuntimeException("Intersecting name: " + data.getName());
        }
        // Валидация
        if (data instanceof FusionCommand.WithSubcommandGroups command) {
            Checks.notEmpty(command.getOptions(), "Subcommand groups");
            Checks.check(command.getOptions().stream()
                            .allMatch(it -> it.getSubcommandData().size() > 0),
                    "All groups must have at least one subcommand");
            Checks.check(command.getOptions().stream()
                            .flatMap(it -> it.getSubcommandData().stream())
                            .allMatch(FusionBaseCommand::hasExecutor),
                    "All subcommands must have an executor");
        } else if (data instanceof FusionCommand.WithSubcommands command) {
            Checks.notEmpty(command.getOptions(), "Subcommands");
            Checks.check(command.getOptions().stream()
                            .allMatch(FusionBaseCommand::hasExecutor),
                    "All subcommands must have an executor");
        } else Checks.check(data.hasExecutor(), "Command must have an executor");
        commands.add(data);
    }

    @Override
    public List<FusionCommand<?>> getCommands() {
        return commands;
    }

    @Override
    public void sendSlashCommands(Guild testingGuild) {
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
}