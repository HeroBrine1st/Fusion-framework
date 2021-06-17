package ru.herobrine1st.fusion.internal.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.declare.FusionBaseCommand;

import java.awt.*;
import java.util.List;
import java.util.*;

public class CommandContextImpl implements CommandContext {
    private static final Logger logger = LoggerFactory.getLogger(CommandContextImpl.class);
    private final Map<String, List<Object>> arguments = new HashMap<>();
    private final Event event;
    private final FusionBaseCommand<?> command;

    public CommandContextImpl(Event event, FusionBaseCommand<?> command) {
        this.event = event;
        this.command = command;
    }

    @Nullable
    @Override
    public Optional<Message> getMessage() {
        if (getEvent() instanceof MessageReceivedEvent messageReceivedEvent) {
            return Optional.of(messageReceivedEvent.getMessage());
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
    public <T> Collection<T> getAll(String key) {
        return null;
    }

    @Override
    public User getAuthor() {
        if (this.getEvent() instanceof MessageReceivedEvent messageReceivedEvent) {
            return messageReceivedEvent.getAuthor();
        } else if(this.getEvent() instanceof SlashCommandEvent slashCommandEvent) {
            return slashCommandEvent.getUser();
        } else {
            logger.error("================== STRANGE THING HAPPENED ==================");
            logger.error("Event is neither MessageReceivedEvent nor SlashCommandEvent");
            logger.error("Exception with a stacktrace will be shown below");
            logger.error("============================================================");
            throw new RuntimeException("Event is neither MessageReceivedEvent nor SlashCommandEvent");
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

    @Override
    public EmbedBuilder getEmbedBase() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(this.getCommand().getShortName());
        return embed;
    }

    @Override
    public Color getColor(int successCount, int totalCount) {
        return switch (getResult(successCount, totalCount)) {
            case SUCCESS -> Color.GREEN;
            case ERROR, PARTIAL_SUCCESS -> Color.RED;
            case UNKNOWN -> Color.BLACK;
        };
    }

    @Override
    public String getFooter(int successCount, int totalCount, String textInFooter) {
        String text = "";
        if (!this.isExecutedAsSlashCommand()) {
            User user = getAuthor();
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
}
