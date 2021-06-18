package ru.herobrine1st.fusion.api.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import ru.herobrine1st.fusion.api.command.declare.FusionBaseCommand;

import java.awt.*;
import java.util.Collection;
import java.util.Optional;

public interface CommandContext {
    Optional<Message> getMessage();
    Event getEvent();
    default JDA getJDA() {
        return getEvent().getJDA();
    }

    void putArg(String key, Object value);
    <T> Optional<T> getOne(String key);
    <T> Collection<T> getAll(String key);

    default boolean isExecutedAsSlashCommand() {
        return getEvent() instanceof SlashCommandEvent;
    }
    User getAuthor();

    FusionBaseCommand<?> getCommand();
    EmbedBuilder getEmbedBase();
    default Color getColor() {
        return getColor(1, 0);
    }
    Color getColor(int successCount, int totalCount);

    default String getFooter(int successCount, int totalCount) {
        return getFooter(successCount, totalCount, "");
    }
    default String getFooter(int successCount) {
        return getFooter(successCount, 0, "");
    }
    default String getFooter(String textInFooter) {
        return getFooter(1, 0, textInFooter);
    }
    default String getFooter() {
        return getFooter(1, 0, "");
    }
    String getFooter(int successCount, int totalCount, String textInFooter);

    void reply(CommandResult result);
}
