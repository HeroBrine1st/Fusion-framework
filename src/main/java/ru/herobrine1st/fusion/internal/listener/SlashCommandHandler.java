package ru.herobrine1st.fusion.internal.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.args.ParserElement;
import ru.herobrine1st.fusion.api.command.build.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandData;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandGroupData;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;
import ru.herobrine1st.fusion.internal.command.context.AbstractCommandContextImpl;
import ru.herobrine1st.fusion.internal.command.context.SlashCommandContext;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class SlashCommandHandler extends ListenerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(SlashCommandHandler.class);

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        final InteractionHook hook = event.getHook().setEphemeral(true);
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
        FusionBaseCommand<?> targetCommand = commandDataOptional.get();
        if (targetCommand.hasSubcommandGroups()) {
            if (groupName == null || subcommandName == null) return; // На невалидный запрос отвечаем невалидным ответом
            var subcommandData = targetCommand.getOptions().stream()
                    .map(it -> (FusionSubcommandGroupData) it)
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
        } else if (targetCommand.hasSubcommands()) {
            if (subcommandName == null) return;
            var subcommandData = targetCommand.getOptions().stream()
                    .map(it -> (FusionSubcommandData) it)
                    .filter(it -> it.getName().equals(subcommandName))
                    .limit(1)
                    .peek(it -> permissionHandlers.add(it.getPermissionHandler()))
                    .findAny();
            if (subcommandData.isEmpty()) return;
            targetCommand = subcommandData.get();
        }
        if(!permissionHandlers.get(0).shouldBeFound(event.getGuild())) {
            return;
        }
        AbstractCommandContextImpl context = new SlashCommandContext(event, targetCommand);
        if (!permissionHandlers.stream().allMatch(it -> it.shouldBeExecuted(context))) {
            event.reply("Нет прав! Требования:\n" + permissionHandlers.stream()
                    .map(it -> it.requirements(context))
                    .collect(Collectors.joining("\n"))
            ).setEphemeral(true).queue();
            return;
        }

        for (ParserElement it : targetCommand.getArguments()) {
            try {
                it.parseSlash(context);
            } catch (ArgumentParseException e) {
                event.reply("Ошибка обработки аргументов!\n" + e.getMessage()).setEphemeral(true).queue();
                return;
            }
        }
        event.deferReply(false).queue();
        try {
            targetCommand.getExecutor().execute(context);
        } catch (Throwable t) {
            context.replyException(t);
        }
    }


}
