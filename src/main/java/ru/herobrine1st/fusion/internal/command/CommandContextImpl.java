package ru.herobrine1st.fusion.internal.command;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.FusionBaseCommand;
import ru.herobrine1st.fusion.internal.listener.ButtonInteractionHandler;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CommandContextImpl implements CommandContext {
    private static final Logger logger = LoggerFactory.getLogger(CommandContextImpl.class);

    private final Map<String, List<Object>> arguments = new HashMap<>();
    private final FusionBaseCommand<?> command;
    private GenericInteractionCreateEvent event;
    private CompletableFuture<ButtonClickEvent> buttonClickEventCompletableFuture = null;

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
    public void putArg(String name, Object value) {
        arguments.computeIfAbsent(name, k -> new ArrayList<>());
        arguments.get(name).add(value);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOne(String name) {
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
    public <T> Collection<T> getAll(String name) {
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
    public CompletableFuture<ButtonClickEvent> waitForButtonClick(Message message) {
        buttonClickEventCompletableFuture = new CompletableFuture<>();
        ButtonInteractionHandler.open(message.getIdLong(), this);
        logger.trace("Opening interaction listener to messageId=%s".formatted(message.getIdLong()));
        return buttonClickEventCompletableFuture;
    }

    public void applyButtonClickEvent(ButtonClickEvent event) { // Да, контекст мутабельный
        if (buttonClickEventCompletableFuture == null) throw new IllegalStateException("No buttons in this context");
        if (buttonClickEventCompletableFuture.isDone()) return;
        this.event = event;
        buttonClickEventCompletableFuture.complete(event);
    }

    public void cancelButtonClickWaiting() {
        buttonClickEventCompletableFuture.cancel(true);
    }
}
