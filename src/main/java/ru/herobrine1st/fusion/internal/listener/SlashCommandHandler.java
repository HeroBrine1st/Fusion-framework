package ru.herobrine1st.fusion.internal.listener;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.declare.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.declare.FusionSubcommandData;
import ru.herobrine1st.fusion.api.command.declare.FusionSubcommandGroupData;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;
import ru.herobrine1st.fusion.api.exception.CommandException;
import ru.herobrine1st.fusion.internal.command.CommandContextImpl;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;

public class SlashCommandHandler extends ListenerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(SlashCommandHandler.class);
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) { // TODO CONCEPT; что-то с optional сделать, явно не все его возможности используются
        event.deferReply(false).queue();
        InteractionHook hook = event.getHook();
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
        FusionBaseCommand<?> targetCommand;
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
        var context = new CommandContextImpl(event, targetCommand, it -> hook.sendMessageEmbeds(it.embed()).queue());
        targetCommand.getArguments().forEach(it -> {
            try {
                it.parseSlash(context);
            } catch (ArgumentParseException e) {
                hook.sendMessage("Ошибка обработки аргументов!\n" + e.getMessage()).queue();
            }
        });
        try {
            targetCommand.getExecutor().execute(context);
        } catch (CommandException e) {
            hook.sendMessage("Ошибка выполнения команды. Дополнительные данные отправлены в журнал.").queue();
            logger.error("Error executing command %s %s %s".formatted(commandName, groupName, subcommandName), e);
        }
    }
}
