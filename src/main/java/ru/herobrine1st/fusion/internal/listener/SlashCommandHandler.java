package ru.herobrine1st.fusion.internal.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.option.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.option.FusionCommand;
import ru.herobrine1st.fusion.api.command.option.parser.ParserElement;
import ru.herobrine1st.fusion.api.exception.CommandException;
import ru.herobrine1st.fusion.api.exception.PermissionException;
import ru.herobrine1st.fusion.internal.command.CommandContextImpl;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

public class SlashCommandHandler implements EventListener {
    private final static Logger logger = LoggerFactory.getLogger(SlashCommandHandler.class);
    private final CommandManagerImpl commandManager;

    public SlashCommandHandler(CommandManagerImpl manager) {
        this.commandManager = manager;
    }

    @SubscribeEvent
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        final String groupName = event.getSubcommandGroup();
        final String subcommandName = event.getSubcommandName();
        final String commandName = event.getName();
        final List<PermissionHandler> permissionHandlers = new ArrayList<>();
        Optional<FusionCommand<?>> commandDataOptional = commandManager.getCommands().stream()
                .filter(it -> it.getName().equals(commandName))
                .limit(1)
                .peek(it -> permissionHandlers.add(it.getPermissionHandler()))
                .findFirst();
        if (commandDataOptional.isEmpty())
            return;
        FusionCommand<?> sourceCommand = commandDataOptional.get();
        FusionBaseCommand<ParserElement<?, ?>> targetCommand;
        permissionHandlers.add(sourceCommand.getPermissionHandler());
        if (sourceCommand instanceof FusionCommand.WithSubcommandGroups command) {
            if (groupName == null || subcommandName == null) return; // На невалидный запрос отвечаем невалидным ответом
            var subcommandData = command.getOptions().stream()
                    .filter(it -> it.getName().equals(groupName))
                    .limit(1)
                    .peek(it -> permissionHandlers.add(it.getPermissionHandler()))
                    .flatMap(it -> it.getSubcommandData().stream())
                    .filter(it -> it.getName().equals(subcommandName))
                    .limit(1)
                    .peek(it -> permissionHandlers.add(it.getPermissionHandler()))
                    .findAny();
            if (subcommandData.isEmpty()) return;
            targetCommand = subcommandData.get();
        } else if (sourceCommand instanceof FusionCommand.WithSubcommands command) {
            if (subcommandName == null) return;
            var subcommandData = command.getOptions().stream()
                    .filter(it -> it.getName().equals(subcommandName))
                    .limit(1)
                    .peek(it -> permissionHandlers.add(it.getPermissionHandler()))
                    .findAny();
            if (subcommandData.isEmpty()) return;
            targetCommand = subcommandData.get();
        } else if (sourceCommand instanceof FusionCommand.WithArguments command) {
            targetCommand = command;
        } else {
            throw new RuntimeException("This fucking won't happen");
        }
        if (permissionHandlers.get(0).shouldNotBeFound(event.getGuild())) {
            return;
        }
        CommandContextImpl context = new CommandContextImpl(event, targetCommand);
        if (!permissionHandlers.stream().allMatch(it -> it.shouldBeExecuted(context))) {
            String requirements = permissionHandlers.stream()
                    .map(it -> it.requirements(context))
                    .collect(Collectors.joining("\n"));
            commandManager.handleException(context,
                    new PermissionException("Not enough permissions to execute this command. Requirements:\n" + requirements, requirements));
            return;
        }

        for (ParserElement<?, ?> it : targetCommand.getOptions()) {
            try {
                it.parseSlash(context);
            } catch (Exception e) {
                commandManager.handleException(context, e);
                return;
            }
        }
        logger.info("Processing %s by %s (%s)".formatted(event.getCommandPath(),
                event.getUser().getAsTag(), event.getUser().getIdLong()));
        try {
            targetCommand.getExecutor().execute(context);
        } catch (Throwable t) {
            if(!event.isAcknowledged()) event.deferReply(true).queue();
            var embed = new EmbedBuilder()
                    .setColor(0xFF0000);
            if (t instanceof CommandException commandException) {
                if(t.getCause() != null) {
                    logger.error(commandException.getMessage(), t.getCause());
                }
                embed.setDescription(commandException.getMessage());
            } else if (t instanceof CancellationException) {
                logger.trace("Caught CancellationException", t);
                return;
            } else if (t instanceof RuntimeException) {
                embed.setDescription("Unknown runtime exception occurred.");
                logger.error("Runtime exception occurred when executing command", t);
            } else {
                embed.setDescription("Unknown exception occurred.");
                logger.error("Error executing command", t);
            }
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof SlashCommandEvent slashCommandEvent) onSlashCommand(slashCommandEvent);
    }
}
