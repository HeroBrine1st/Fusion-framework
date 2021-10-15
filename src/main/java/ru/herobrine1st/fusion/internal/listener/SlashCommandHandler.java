package ru.herobrine1st.fusion.internal.listener;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.args.parser.ParserElement;
import ru.herobrine1st.fusion.api.command.build.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.build.FusionCommand;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;
import ru.herobrine1st.fusion.api.manager.CommandManager;
import ru.herobrine1st.fusion.internal.command.CommandContextImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SlashCommandHandler {
    private final static Logger logger = LoggerFactory.getLogger(SlashCommandHandler.class);
    private final CommandManager commandManager;

    public SlashCommandHandler(CommandManager manager) {
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
        FusionBaseCommand<?, ParserElement<?, ?>> targetCommand;
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
            throw new RuntimeException(); // TODO Sealed class
        }
        if (permissionHandlers.get(0).shouldNotBeFound(event.getGuild())) {
            return;
        }
        CommandContextImpl context = new CommandContextImpl(event, targetCommand);
        if (!permissionHandlers.stream().allMatch(it -> it.shouldBeExecuted(context))) {
            event.reply("Нет прав! Требования:\n" + permissionHandlers.stream()
                    .map(it -> it.requirements(context))
                    .collect(Collectors.joining("\n"))
            ).setEphemeral(true).queue();
            return;
        }

        for (ParserElement<?, ?> it : targetCommand.getOptions()) {
            try {
                it.parseSlash(context);
            } catch (ArgumentParseException e) {
                event.reply("Ошибка обработки аргументов!\n" + e.getMessage()).setEphemeral(true).queue();
                return;
            }
        }
        event.deferReply(false).queue();
        logger.info("Processing %s %s %s by %s (%s)".formatted(commandName, groupName, subcommandName,
                event.getUser().getAsTag(), event.getUser().getIdLong()));
        try {
            targetCommand.getExecutor().execute(context);
        } catch (Throwable t) {
            context.replyException(t);
        }
    }
}
