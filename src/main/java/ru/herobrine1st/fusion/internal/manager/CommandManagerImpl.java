package ru.herobrine1st.fusion.internal.manager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
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

    public List<FusionCommand<?>> getCommands() {
        return commands;
    }

    @Override
    public CommandListUpdateAction updateCommands(@NotNull Guild guild) {
        return guild.updateCommands()
                .addCommands(commands.stream()
                        .map(SlashCommandBuilder::buildCommand)
                        .toList());
    }

    public CommandListUpdateAction updateCommands() {
        return jda.updateCommands()
                .addCommands(commands.stream()
                        .map(SlashCommandBuilder::buildCommand)
                        .toList());
    }

    @Override
    public void registerListeners() {
        jda.addEventListener(new SlashCommandHandler(this));
        if(!jda.getEventManager().getRegisteredListeners().contains(ButtonInteractionHandler.INSTANCE)) {
            jda.addEventListener(ButtonInteractionHandler.INSTANCE);
        }
    }

    @Override
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void handleException(CommandContext ctx, Exception exception) {
        exceptionHandler.handle(ctx, exception);
    }
}