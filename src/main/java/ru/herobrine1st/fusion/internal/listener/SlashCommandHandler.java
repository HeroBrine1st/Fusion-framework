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
import ru.herobrine1st.fusion.api.command.declare.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.declare.FusionSubcommandData;
import ru.herobrine1st.fusion.api.command.declare.FusionSubcommandGroupData;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;
import ru.herobrine1st.fusion.internal.command.CommandContextImpl;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class SlashCommandHandler extends ListenerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(SlashCommandHandler.class);
    private final Map<Long, CommandContextImpl> interactionCache = new HashMap<>();
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) { // TODO CONCEPT; что-то с optional сделать, явно не все его возможности используются
        InteractionHook hook = event.getHook().setEphemeral(true);
        String groupName = event.getSubcommandGroup();
        String subcommandName = event.getSubcommandName();
        String commandName = event.getName();
        var command = CommandManagerImpl.INSTANCE.commands.stream()
                .filter(it -> it.getName().equals(commandName))
                .findFirst();
        if (command.isEmpty()) {
            hook.sendMessage("Команда не найдена").queue();
            return;
        }
        FusionBaseCommand<?> targetCommand; // TODO говнокод
        if (groupName != null) {
            if(!command.get().hasSubcommandGroups()) {
                hook.sendMessage("Команда не имеет групп субкоманд").queue();
            }
            var group = command.get().getOptions().stream().map(it -> (FusionSubcommandGroupData) it)
                    .filter(it -> it.getName().equals(groupName))
                    .findFirst();
            if (group.isEmpty()) {
                hook.sendMessage("Группа субкоманд не найдена").queue();
                return;
            }
            var subcommandOptional = group.get().getSubcommandData().stream()
                    .filter(it -> it.getName().equals(subcommandName))
                    .findFirst();
            if (subcommandOptional.isEmpty()) {
                hook.sendMessage("Субкоманда не найдена").queue();
                return;
            }
            targetCommand = subcommandOptional.get();
        } else if (subcommandName != null) {
            if(!command.get().hasSubcommands()) {
                hook.setEphemeral(true).sendMessage("Команда не имеет субкоманд").queue();
            }
            var subcommandOptional = command.get().getOptions().stream().map(it -> (FusionSubcommandData) it)
                    .filter(it -> it.getName().equals(subcommandName))
                    .findFirst();
            if (subcommandOptional.isEmpty()) {
                hook.sendMessage("Субкоманда не найдена").queue();
                return;
            }
            targetCommand = subcommandOptional.get();
        } else {
            targetCommand = command.get();
        }
        BiFunction<Message, CommandContextImpl, RestAction<Message>> replyHandler = (message, ctx) -> {
            var rows = message.getActionRows();
            return hook.sendMessage(message).map(msg -> {
                if(!rows.isEmpty()) {
                    interactionCache.put(msg.getIdLong(), ctx);
                }
                return msg;
            });
        };

        CommandContextImpl context = new CommandContextImpl(event, targetCommand, replyHandler);
        if(!targetCommand.getPermissionHandler().shouldBeExecuted(context)) {
            hook.sendMessage("Нет прав!").queue();
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
        var hook = event.getHook();
        if(!interactionCache.containsKey(event.getMessageIdLong())) {
            hook.setEphemeral(true).sendMessage("Данное сообщение отсуствует в кеше.").queue();
            return;
        }
        var context = interactionCache.get(event.getMessageIdLong());
        if(context.getUser().getIdLong() != event.getUser().getIdLong()) {
            hook.setEphemeral(true).sendMessage("Вы не являетесь автором команды.").queue();
            return;
        }
        if(event.getMessage() != null) {
            if(event.getMessage().getButtonById(event.getComponentId()) == null) {
                return;
            }
        }
        interactionCache.remove(event.getMessageIdLong());
        event.deferReply().queue();
        logger.info(String.valueOf(event.getMessage()));
        context.applyButtonClickEvent(event, (message, ctx) -> hook.sendMessage(message));
    }
}
