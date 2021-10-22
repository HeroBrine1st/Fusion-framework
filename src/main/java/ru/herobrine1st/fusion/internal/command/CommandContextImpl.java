package ru.herobrine1st.fusion.internal.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.build.FusionBaseCommand;
import ru.herobrine1st.fusion.internal.listener.ButtonInteractionHandler;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CommandContextImpl implements CommandContext {
    private static final Logger logger = LoggerFactory.getLogger(CommandContextImpl.class);

    private final Map<String, List<Object>> arguments = new HashMap<>();
    private final FusionBaseCommand<?, ?> command;
    private Event event;
    private CompletableFuture<ButtonClickEvent> buttonClickEventCompletableFuture = null;

    public CommandContextImpl(Event event, FusionBaseCommand<?, ?> command) {
        this.event = event;
        this.command = command;
    }

    @Override
    public Event getEvent() {
        return event;
    }

    @Override
    public InteractionHook getHook() {
        if(event instanceof SlashCommandEvent slashCommandEvent) {
            return slashCommandEvent.getHook();
        } else if (event instanceof ButtonClickEvent buttonClickEvent) {
            return buttonClickEvent.getHook();
        }
        throw new IllegalStateException("Unexpected event");
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
        if (event instanceof SlashCommandEvent slashCommandEvent) {
            return slashCommandEvent.getUser();
        } else if (event instanceof ButtonClickEvent buttonClickEvent) {
            return buttonClickEvent.getUser();
        }
        throw new IllegalStateException("Unexpected event");
    }

    @Override
    public FusionBaseCommand<?, ?> getCommand() {
        return command;
    }

    enum Type {
        SUCCESS,
        ERROR,
        PARTIAL_SUCCESS,
        UNKNOWN
    }

    @Override
    public EmbedBuilder getEmbedBase() {
        return new EmbedBuilder()
                .setTitle(getCommand().getShortName())
                .setColor(getColor());
    }

    static Type getResult(int successCount, int totalCount) {
        if (successCount != 0) {
            if (totalCount != 0) {
                if ((double) successCount / (double) totalCount >= 0.5)
                    return Type.SUCCESS; // успешно больше нуля, всего больше нуля, успешно больше половины
                else
                    return Type.PARTIAL_SUCCESS; // успешно больше нуля, всего больше нуля, успешно меньше половины
            } else return Type.SUCCESS; // успешно больше нуля, всего нуль
        } else if (totalCount != 0) {
            return Type.ERROR; // Успешно ноль, всего больше нуля
        }
        return Type.UNKNOWN;
    }

    public static Color getEmbedColor(int successCount, int totalCount) {
        return switch (getResult(successCount, totalCount)) {
            case SUCCESS -> Color.GREEN;
            case ERROR, PARTIAL_SUCCESS -> Color.RED;
            case UNKNOWN -> Color.BLACK;
        };
    }

    @Override
    public Color getColor(int successCount, int totalCount) {
        return getEmbedColor(successCount, totalCount);
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
