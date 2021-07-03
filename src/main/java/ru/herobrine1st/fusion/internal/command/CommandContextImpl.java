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
import ru.herobrine1st.fusion.api.restaction.CompletableFutureRestAction;
import ru.herobrine1st.fusion.internal.listener.ButtonInteractionHandler;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class CommandContextImpl implements CommandContext {
    private static final Logger logger = LoggerFactory.getLogger(CommandContextImpl.class);

    private final Map<String, List<Object>> arguments = new HashMap<>();
    private final FusionBaseCommand<?> command;
    protected Event event;
    private CompletableFuture<ButtonClickEvent> buttonClickEventCompletableFuture = null;

    public CommandContextImpl(Event event, FusionBaseCommand<?> command) {
        this.event = event;
        this.command = command;
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
        if (this.getEvent() instanceof MessageReceivedEvent messageReceivedEvent) {
            return messageReceivedEvent.getAuthor();
        } else if (this.getEvent() instanceof SlashCommandEvent slashCommandEvent) {
            return slashCommandEvent.getUser();
        } else if (this.getEvent() instanceof ButtonClickEvent buttonClickEvent) {
            return buttonClickEvent.getUser();
        }
        throw new RuntimeException("Unexpected event");
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

    public RestAction<ButtonClickEvent> getButtonClickEventRestAction() {
        Objects.requireNonNull(buttonClickEventCompletableFuture, "No buttons in this context");
        return CompletableFutureRestAction.of(buttonClickEventCompletableFuture);
    }

    @Override
    public CompletableFuture<ButtonClickEvent> getButtonClickEventCompletableFuture() {
        Objects.requireNonNull(buttonClickEventCompletableFuture, "No buttons in this context");
        return buttonClickEventCompletableFuture;
    }

    public void applyButtonClickEvent(ButtonClickEvent event) { // Да, контекст мутабельный
        Objects.requireNonNull(buttonClickEventCompletableFuture, "No buttons in this context");
        if (buttonClickEventCompletableFuture.isDone()) return;
        this.event = event;
        buttonClickEventCompletableFuture.complete(event);
    }

    public void cancelButtonClickWaiting() {
        buttonClickEventCompletableFuture.cancel(true);
    }

    private RestAction<Message> handleReply(Message message) {
        if (event instanceof MessageReceivedEvent messageReceivedEvent) {
            return messageReceivedEvent.getMessage().reply(message)
                    .mentionRepliedUser(false);
        } else if (event instanceof SlashCommandEvent slashCommandEvent) {
            return slashCommandEvent.getHook().sendMessage(message);
        } else if (event instanceof ButtonClickEvent buttonClickEvent) {
            return buttonClickEvent.getHook().sendMessage(message);
        }
        throw new RuntimeException("Unexpected event");
    }

    @Override
    public RestAction<Message> reply(Message message) {
        if (!message.getActionRows().isEmpty()) {
            buttonClickEventCompletableFuture = new CompletableFuture<>();
        }
        return handleReply(message).map(msg -> {
            if (!message.getActionRows().isEmpty()) {
                ButtonInteractionHandler.open(msg.getIdLong(), this);
                logger.trace("Opening interaction listener to messageId=%s".formatted(msg.getIdLong()));
            }
            return msg;
        });
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
        } else if (t instanceof CancellationException) {
            return;
        } else if (t instanceof RuntimeException) {
            embed.setDescription("Неизвестная ошибка. Дополнительные данные отправлены в журнал.");
            logger.error("Runtime exception occurred when executing command", t);
        } else {
            embed.setDescription("Неизвестная ошибка. Дополнительные данные отправлены в журнал.");
            logger.error("Error executing command", t);
        }
        reply(new MessageBuilder().setEmbed(embed.build()).build()).queue();
    }
}
