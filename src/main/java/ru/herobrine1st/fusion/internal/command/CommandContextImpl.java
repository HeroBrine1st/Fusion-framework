package ru.herobrine1st.fusion.internal.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.build.FusionBaseCommand;
import ru.herobrine1st.fusion.api.exception.CommandException;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

public class CommandContextImpl implements CommandContext {
    private static final Logger logger = LoggerFactory.getLogger(CommandContextImpl.class);

    private final Map<String, List<Object>> arguments = new HashMap<>();
    private final FusionBaseCommand<?> command;
    private Event event;
    private BiFunction<Message, CommandContextImpl, RestAction<Message>> replyHandler;
    private CompletableFuture<ButtonClickEvent> buttonClickEventCompletableFuture = null;

    public CommandContextImpl(Event event, FusionBaseCommand<?> command, BiFunction<Message, CommandContextImpl, RestAction<Message>> replyHandler) {
        this.event = event;
        this.command = command;
        this.replyHandler = replyHandler;
    }

    @Nullable
    @Override
    public Optional<Message> getMessage() {
        if (getEvent() instanceof MessageReceivedEvent messageReceivedEvent) {
            return Optional.of(messageReceivedEvent.getMessage());
        } else if (getEvent() instanceof ButtonClickEvent buttonClickEvent) {
            return Optional.ofNullable(buttonClickEvent.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Event getEvent() {
        return event;
    }

    @Override
    public void putArg(String key, Object value) {
        arguments.computeIfAbsent(key, k -> new ArrayList<>());
        arguments.get(key).add(value);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOne(String key) {
        var list = arguments.get(key);
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
    public <T> Collection<T> getAll(String key) {
        return (Collection<T>) arguments.getOrDefault(key, Collections.emptyList());
    }

    @Override
    public User getUser() {
        if (this.getEvent() instanceof MessageReceivedEvent messageReceivedEvent) {
            return messageReceivedEvent.getAuthor();
        } else if (this.getEvent() instanceof SlashCommandEvent slashCommandEvent) {
            return slashCommandEvent.getUser();
        } else if (this.getEvent() instanceof ButtonClickEvent buttonClickEvent) {
            return buttonClickEvent.getUser();
        } else {
            logger.error("============================= STRANGE THING HAPPENED =============================");
            logger.error("Event is neither MessageReceivedEvent, nor SlashCommandEvent, nor ButtonClickEvent");
            logger.error("Exception with stacktrace will be shown below");
            logger.error("==================================================================================");
            throw new RuntimeException("Unexpected event");
        }
    }

    @Override
    public FusionBaseCommand<?> getCommand() {
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
                .setFooter(getFooter())
                .setColor(getColor());
    }

    static Type getResult(int successCount, int totalCount) {
        if (successCount != 0) {
            if (totalCount != 0) {
                if ((double) successCount / (double) totalCount >= 0.5)
                    return Type.SUCCESS; // успешно больше нуля, всего больше нуля, успешно больше половины
                else
                    return Type.PARTIAL_SUCCESS; // успешно больше нуля, всего больше нуля, успешно меньше половины
            } else return Type.SUCCESS; // успешно больше нуля, всего ноль
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
    public String getFooter(int successCount, int totalCount, String textInFooter) {
        String text = "";
        if (!this.isExecutedAsSlashCommand()) {
            User user = getUser();
            text += String.format("Запросил: %s\n", user.getAsTag());
        }
        if (successCount > 0 && (successCount != 1 || totalCount > 0)) {
            text += "Выполнено: " + successCount;
            if (totalCount > 0) {
                text += "/" + totalCount;
            }
            text += "\n";
        }
        if (!textInFooter.isBlank())
            text += textInFooter;
        return text;
    }

    @Override
    public ButtonClickEvent waitForButtonClick() throws CancellationException {
        Objects.requireNonNull(buttonClickEventCompletableFuture, "No buttons in this context");
        try {
            return buttonClickEventCompletableFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Strange exception occurred", e); // Should not occur
            throw new RuntimeException(e);
        }
    }

    public void applyButtonClickEvent(ButtonClickEvent event, BiFunction<Message, CommandContextImpl, RestAction<Message>> replyHandler) {
        Objects.requireNonNull(buttonClickEventCompletableFuture, "No buttons in this context");
        if (buttonClickEventCompletableFuture.isDone()) return;
        this.replyHandler = replyHandler;
        this.event = event;
        buttonClickEventCompletableFuture.complete(event);
    }

    @Override
    public RestAction<Message> reply(Message message) {
        if(!message.getActionRows().isEmpty()) buttonClickEventCompletableFuture = new CompletableFuture<>();
        return replyHandler.apply(message, this);
    }

    @Override
    public RestAction<Message> reply(MessageEmbed embed) {
        return reply(new MessageBuilder().setEmbed(embed).build());
    }

    @Override
    public RestAction<Message> reply(MessageEmbed embed, ActionRow... rows) {
        return reply(new MessageBuilder()
                .setEmbed(embed)
                .setActionRows(rows)
                .build());
    }

    @Override
    public void replyException(Throwable t) {
        var embed = getEmbedBase()
                .setColor(getColor(0, 1))
                .setFooter(getFooter(0, 1));
        if (t instanceof CommandException) {
            embed.setDescription("Ошибка выполнения команды: " + t.getMessage());
        } else if(t instanceof CancellationException) {
            return;
        } else if(t instanceof RuntimeException) {
            logger.trace("Runtime exception occurred when executing command", t);
            return;
        } else {
            embed.setDescription("Неизвестная ошибка. Дополнительные данные отправлены в журнал.");
            logger.error("Error executing command", t);
        }
        reply(new MessageBuilder().setEmbed(embed.build()).build()).queue();
    }
}
