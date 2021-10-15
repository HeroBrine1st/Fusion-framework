package ru.herobrine1st.fusion.internal.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
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
import ru.herobrine1st.fusion.internal.command.args.CommandArgsImpl;

import java.util.*;
import java.util.stream.Collectors;

public class MessageCommandHandler implements EventListener {
    private final static Logger logger = LoggerFactory.getLogger(MessageCommandHandler.class);
    private final CommandManager commandManager;
    public MessageCommandHandler(CommandManager manager) {
        this.commandManager = manager;
    }

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isBot())
            return;
        if (!event.getMessage().getContentRaw().startsWith(commandManager.getCommandPrefix()))
            return;
        CommandArgsImpl args = new CommandArgsImpl(event.getMessage().getContentRaw().substring(commandManager.getCommandPrefix().length()));
        if (!args.hasNext())
            return;
        String commandName = args.next().getValue();
        final List<PermissionHandler> permissionHandlers = new ArrayList<>();
        Optional<FusionCommand<?>> commandDataOptional = commandManager.getCommands().stream()
                .filter(it -> it.getName().equals(commandName))
                .limit(1)
                .peek(it -> permissionHandlers.add(it.getPermissionHandler()))
                .findFirst();
        if (commandDataOptional.isEmpty()) {
            return;
        }
        FusionCommand<?> sourceCommand = commandDataOptional.get();
        FusionBaseCommand<?, ParserElement<?, ?>> targetCommand;
        permissionHandlers.add(sourceCommand.getPermissionHandler());
        if (sourceCommand instanceof FusionCommand.WithSubcommandGroups command) {
            String groupName;
            String subcommandName;
            try {
                groupName = args.next().value();
                subcommandName = args.next().value();
            } catch (NoSuchElementException e) {
                return;
            }
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
            String subcommandName;
            try {
                subcommandName = args.next().value();
            } catch (NoSuchElementException e) {
                return;
            }
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
        if (!permissionHandlers.get(0).commandType().classicExecutionPermitted()) {
            return;
        }
        CommandContextImpl context = new CommandContextImpl(event, targetCommand);
        if (!permissionHandlers.stream().allMatch(it -> it.shouldBeExecuted(context))) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Нет прав!")
                    .setDescription("Требования:\n" + permissionHandlers.stream()
                            .map(it -> it.requirements(context))
                            .collect(Collectors.joining("\n")))
                    .setFooter(String.format("Запросил: %s\n", event.getAuthor().getAsTag()))
                    .setColor(CommandContextImpl.getEmbedColor(0, 1))
                    .build()).queue();
            return;
        }
        for (ParserElement<?, ?> it : targetCommand.getOptions()) {
            try {
                it.parse(args, context);
            } catch (ArgumentParseException e) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Ошибка распознавания аргументов!")
                        .setDescription(Objects.requireNonNullElse(e.getMessage(), "Неизвестная ошибка!"))
                        .setFooter(String.format("Запросил: %s\n", event.getAuthor().getAsTag()))
                        .setColor(CommandContextImpl.getEmbedColor(0, 1))
                        .build()).queue();
                return;
            }
        }
        logger.info("Processing %s by %s (%s)".formatted(event.getMessage().getContentRaw(),
                event.getAuthor().getAsTag(), event.getAuthor().getIdLong()));
        try {
            targetCommand.getExecutor().execute(context);
        } catch (Throwable t) {
            context.replyException(t);
        }
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof MessageReceivedEvent messageReceivedEvent) onMessageReceived(messageReceivedEvent);
    }
}
