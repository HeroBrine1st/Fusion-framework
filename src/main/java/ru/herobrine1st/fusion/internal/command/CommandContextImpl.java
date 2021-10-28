package ru.herobrine1st.fusion.internal.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.option.FusionBaseCommand;
import ru.herobrine1st.fusion.api.exception.CommandException;
import ru.herobrine1st.fusion.internal.listener.ButtonInteractionHandler;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class CommandContextImpl implements CommandContext {
    private static final Logger logger = LoggerFactory.getLogger(CommandContextImpl.class);

    private final Map<String, List<Object>> arguments = new HashMap<>();
    private final FusionBaseCommand<?> command;
    private GenericInteractionCreateEvent event;
    private CompletableFuture<ButtonClickEvent> buttonClickEventCompletableFuture = null;
    private boolean waitingForComponentInteraction = false;

    public CommandContextImpl(GenericInteractionCreateEvent event, FusionBaseCommand<?> command) {
        this.event = event;
        this.command = command;
    }

    @Override
    public GenericInteractionCreateEvent getEvent() {
        return event;
    }

    @Override
    public InteractionHook getHook() {
        return event.getHook();
    }

    @Override
    public InteractionCallbackAction deferReply(boolean ephemeral) {
        if(event instanceof ComponentInteraction componentInteraction) {
            return componentInteraction.deferEdit();
        } else {
            return event.deferReply(ephemeral);
        }
    }

    @Override
    public void putArg(String name, Object value) {
        arguments.computeIfAbsent(name, k -> new ArrayList<>());
        arguments.get(name).add(value);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getArgument(String name) {
        var list = arguments.get(name);
        if (list != null && list.size() > 0) {
            Object argument = list.get(0);
            // Нет, это не костыль. Какой там будет тип компилятор не поймет, хотя на компилтайме можно понять.
            return Optional.of((T) argument);
        } else {
            return Optional.empty();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getArguments(String name) {
        return (Collection<T>) arguments.getOrDefault(name, Collections.emptyList());
    }

    @Override
    public User getUser() {
        return event.getUser();
    }

    @Override
    public FusionBaseCommand<?> getCommand() {
        return command;
    }



    @Override
    public CompletableFuture<ButtonClickEvent> waitForComponentInteraction(Message message) {
        submitComponents(message, true);
        return buttonClickEventCompletableFuture;
    }

    @Override
    public Message submitComponents(Message message) {
        submitComponents(message, false);
        return message;
    }

    // Защита от долбоёба. Тут конечно ни хуя не случится, но всякое бывает
    private final Object lock = new Object();

    private void submitComponents(Message message, boolean createCompletableFuture) {
        synchronized (lock) {
            if (message.getActionRows().isEmpty()) throw new IllegalArgumentException("No action rows in message");
            if (waitingForComponentInteraction)
                throw new IllegalStateException("Already waiting for component interaction");
            ButtonInteractionHandler.open(message.getIdLong(), this);
            logger.trace("Opening interaction listener to messageId=%s".formatted(message.getIdLong()));
            waitingForComponentInteraction = true;
            if (createCompletableFuture)
                this.buttonClickEventCompletableFuture = new CompletableFuture<>();
        }
    }

    public void applyButtonClickEvent(ButtonClickEvent event) { // Да, контекст мутабельный
        synchronized (lock) {
            if (!waitingForComponentInteraction) return;
            waitingForComponentInteraction = false;
            this.event = event;
            if (buttonClickEventCompletableFuture != null) { //
                if (buttonClickEventCompletableFuture.isDone()) return;
                buttonClickEventCompletableFuture.complete(event);
            } else {
                execute();
            }
        }
    }

    public void cancelButtonClickWaiting() {
        synchronized (lock) {
            if (!waitingForComponentInteraction) return;
            waitingForComponentInteraction = false;
            if (buttonClickEventCompletableFuture != null) {
                buttonClickEventCompletableFuture.cancel(true);
                buttonClickEventCompletableFuture = null;
            }
        }
    }

    public void execute() {
        try {
            this.getCommand().getExecutor().execute(this);
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
}