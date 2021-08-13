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
import ru.herobrine1st.fusion.api.command.build.FusionSubcommand;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandGroup;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;
import ru.herobrine1st.fusion.internal.command.CommandContextImpl;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SlashCommandHandler {
    private final static Logger logger = LoggerFactory.getLogger(SlashCommandHandler.class);

    @SubscribeEvent
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        final String groupName = event.getSubcommandGroup();
        final String subcommandName = event.getSubcommandName();
        final String commandName = event.getName();
        final List<PermissionHandler> permissionHandlers = new ArrayList<>();
        var commandDataOptional = CommandManagerImpl.INSTANCE.commands.stream()
                .filter(it -> it.getName().equals(commandName))
                .limit(1)
                .peek(it -> permissionHandlers.add(it.getPermissionHandler()))
                .findFirst();
        if (commandDataOptional.isEmpty())
            return;
        FusionCommand<?> sourceCommand = commandDataOptional.get();
        FusionBaseCommand<?, ParserElement<?, ?>> targetCommand;
        permissionHandlers.add(sourceCommand.getPermissionHandler());
        if (sourceCommand.hasSubcommandGroups()) {
            if (groupName == null || subcommandName == null) return; // На невалидный запрос отвечаем невалидным ответом
            var subcommandData = sourceCommand.getOptions().stream()
                    .map(it -> (FusionSubcommandGroup) it)
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
        } else if (sourceCommand.hasSubcommands()) {
            if (subcommandName == null) return;
            var subcommandData = sourceCommand.getOptions().stream()
                    .map(it -> (FusionSubcommand) it)
                    .filter(it -> it.getName().equals(subcommandName))
                    .limit(1)
                    .peek(it -> permissionHandlers.add(it.getPermissionHandler()))
                    .findAny();
            if (subcommandData.isEmpty()) return;
            targetCommand = subcommandData.get();
        } else {
            // Can't check type of R because of type erasure, but sure it's always the ParserElement
            //noinspection unchecked
            targetCommand = (FusionBaseCommand<?, ParserElement<?, ?>>) sourceCommand;
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
