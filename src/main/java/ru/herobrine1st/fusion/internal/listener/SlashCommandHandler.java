package ru.herobrine1st.fusion.internal.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.build.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandData;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandGroupData;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;
import ru.herobrine1st.fusion.internal.command.CommandContextImpl;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class SlashCommandHandler extends ListenerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(SlashCommandHandler.class);
    private final Map<Long, CommandContextImpl> interactionCache = new HashMap<>();

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) { // TODO CONCEPT; что-то с optional сделать, явно не все его возможности используются
        final InteractionHook hook = event.getHook().setEphemeral(true);
        final String groupName = event.getSubcommandGroup();
        final String subcommandName = event.getSubcommandName();
        final String commandName = event.getName();
        final List<PermissionHandler> permissionHandlers = new ArrayList<>();
        var command = CommandManagerImpl.INSTANCE.commands.stream()
                .filter(it -> it.getName().equals(commandName))
                .limit(1)
                .peek(it -> permissionHandlers.add(it.getPermissionHandler()))
                .findFirst();
        if (command.isEmpty()) return;
        FusionBaseCommand<?> targetCommand = command.get();
        if (targetCommand.hasSubcommandGroups()) {
            if (groupName == null || subcommandName == null) return;
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
        if (!targetCommand.hasExecutor()) {
            logger.error("Command %s has no executor".formatted(targetCommand.getClass().getCanonicalName()));
            return;
        }
        BiFunction<Message, CommandContextImpl, RestAction<Message>> replyHandler =
                (message, ctx) -> hook.sendMessage(message).map(msg -> {
                    if (!message.getActionRows().isEmpty()) interactionCache.put(msg.getIdLong(), ctx);
                    return msg;
                });
        CommandContextImpl context = new CommandContextImpl(event, targetCommand, replyHandler);
        if (!permissionHandlers.stream().allMatch(it -> it.shouldBeExecuted(context))) {
            hook.sendMessage("Нет прав! Требования:\n" + permissionHandlers.stream()
                    .map(it -> it.requirements(context))
                    .collect(Collectors.joining("\n"))
            ).queue();
            return;
        }

        targetCommand.getArguments().forEach(it -> {
            try {
                it.parseSlash(context);
            } catch (ArgumentParseException e) {
                hook.sendMessage("Ошибка обработки аргументов!\n" + e.getMessage()).queue();
            }
        });
        event.deferReply(false).queue();
        try {
            targetCommand.getExecutor().execute(context);
        } catch (Throwable t) {
            context.replyException(t);
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        // TODO отмена через 5 минут
        if (!interactionCache.containsKey(event.getMessageIdLong())) {
            event.reply("Данное сообщение больше не принимает взаимодействий.").setEphemeral(true).queue();
            return;
        }
        var context = interactionCache.get(event.getMessageIdLong());
        if (context.getUser().getIdLong() != event.getUser().getIdLong()) {
            event.reply("Вы не являетесь автором команды.").setEphemeral(true).queue();
            return;
        }
        if (event.getMessage() != null) {
            if (event.getMessage().getButtonById(event.getComponentId()) == null) {
                return;
            }
        }
        interactionCache.remove(event.getMessageIdLong());
        event.deferReply().queue();
        var hook = event.getHook();
        context.applyButtonClickEvent(event, (message, ctx) -> hook.editOriginal(message));
    }
}
