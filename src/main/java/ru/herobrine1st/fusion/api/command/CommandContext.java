package ru.herobrine1st.fusion.api.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.RestAction;
import ru.herobrine1st.fusion.api.command.build.FusionBaseCommand;

import java.awt.*;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CancellationException;

public interface CommandContext {
    Optional<Message> getMessage();

    Event getEvent();

    default JDA getJDA() {
        return getEvent().getJDA();
    }

    void putArg(String key, Object value);

    <T> Optional<T> getOne(String name);

    <T> Collection<T> getAll(String name);

    default boolean isExecutedAsSlashCommand() {
        return getEvent() instanceof SlashCommandEvent;
    }

    User getUser();

    FusionBaseCommand<?> getCommand();

    EmbedBuilder getEmbedBase();

    default Color getColor() {
        return getColor(1, 0);
    }

    Color getColor(int successCount, int totalCount);

    default Color getErrorColor() {
        return getColor(0, 1);
    }

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

    ButtonClickEvent waitForButtonClick() throws CancellationException;

    RestAction<Message> reply(Message message);

    RestAction<Message> reply(MessageEmbed embed);

    RestAction<Message> reply(MessageEmbed embed, ActionRow... rows);

    default RestAction<ButtonClickEvent> replyWaitingClick(MessageEmbed embed, ActionRow... rows) {
        return reply(embed, rows).map(it -> waitForButtonClick());
    }

    /**
     * Use this on queue as second argument
     * <h2>Example:</h2>
     * <pre><code>
     * ctx -> ctx
     *     .{@link #replyWaitingClick(MessageEmbed, ActionRow... rows) replyWaitingClick}(ctx.{@link #getEmbedBase() getEmbedBase}()
     *             .setDescription("Description)
     *             .build(),
     *         ActionRow.of(Button.primary("button", "Example button 1")))
     *     .flatMap(event -> { ... })
     *     // ..other calls, if any
     *     .queue(null, ctx::replyException)
     * </code></pre>
     */
    void replyException(Throwable t);


}
