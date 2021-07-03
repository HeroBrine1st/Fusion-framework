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
import ru.herobrine1st.fusion.api.exception.CommandException;

import java.awt.*;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * A class representing context of command execution.<br>
 * This is used to get various information of execution and replying no matter what event triggered execution.<br>
 * <br>
 * Methods that return an instance of {@link RestAction} (for example, {@link #reply(Message)}) require an additional step to complete the execution.
 * Learn more {@link RestAction here}<br>
 * <br>
 * <h2>Example of simple Hello, user! command:</h2>
 * <pre><code>
 *     class HelloUserCommand implements {@link CommandExecutor} {
 *         public void execute(CommandContext ctx) throws {@link CommandException} {
 *             ctx.{@link #reply(String) reply}("Hello, " + ctx.{@link #getUser() getUser}().getName() + "!").queue();
 *         }
 *     }
 * </code></pre>
 * All methods in this class return nonnull values unless annotated with @{@link javax.annotation.Nullable Nullable}<br>
 */
public interface CommandContext {
    /**
     * Message that triggered this command execution. Empty if no message available (Slash interaction and, possibly, button interaction)
     *
     * @return Optional container with Message object
     */
    Optional<Message> getMessage();

    /**
     * Event that triggered command this command execution.
     *
     * @return {@link Event} object. Instance of either {@link net.dv8tion.jda.api.events.message.MessageReceivedEvent MessageReceivedEvent}, {@link SlashCommandEvent} or {@link ButtonClickEvent}
     */
    Event getEvent();

    /**
     * JDA API object
     *
     * @return {@link JDA} object
     */
    default JDA getJDA() {
        return getEvent().getJDA();
    }

    /**
     * Put argument into this context. Usually used by ParserElements to add parsed argument to context.<br>
     * <b>Rarely used inside {@link CommandExecutor}, do not use this if you don't know what is it!</b>
     * @param name  name of the argument
     * @param value value of the argument
     */
    void putArg(String name, Object value);

    /**
     * Get argument with certain name from context
     *
     * @param name name of the argument
     * @param <T>  type of the argument. Will be <b>unsafely casted</b>, so make sure you did all right!
     * @return {@link Optional}<T> container with argument
     * @see #getAll(String)
     */
    <T> Optional<T> getOne(String name);

    /**
     * Get collection of arguments with certain name from context
     *
     * @param name name of the argument
     * @param <T>  type of the argument. Will be <b>unsafely casted</b>, so make sure you did all right!
     * @return {@link Collection}<T> of arguments
     * @see #getOne(String)
     */
    <T> Collection<T> getAll(String name);

    /**
     * Whether this execution is triggered by slash interation or not.<br>
     * This method is a shortcut for {@code {@link #getEvent() getEvent}() instanceof {@link SlashCommandEvent}}
     *
     * @return true if triggered by a slash interaction
     */
    default boolean isExecutedAsSlashCommand() {
        return getEvent() instanceof SlashCommandEvent;
    }

    /**
     * User that triggered this execution
     *
     * @return {@link User} object
     */
    User getUser();

    /**
     * Command executing in this execution
     *
     * @return {@link FusionBaseCommand}<?> object describing command that (usually) called this method.
     */
    FusionBaseCommand<?> getCommand();

    /**
     * EmbedBuilder that you should use as base for your final embed.
     *
     * @return {@link EmbedBuilder} object with filled color, title and (if this is message execution) footer
     */
    EmbedBuilder getEmbedBase();

    /**
     * This method is a shortcut for {@code getEmbedBase().setDescription(description)}
     *
     * @param description description that will be set in embed
     * @return {@link EmbedBuilder} object
     * @see #getEmbedBase()
     */
    default EmbedBuilder getEmbedBase(String description) {
        return getEmbedBase().setDescription(description);
    }

    /**
     * Color for use in embed of success
     *
     * @return {@link java.awt.Color} object
     * @see #getColor(int, int)
     */
    default Color getColor() {
        return getColor(1, 0);
    }

    /**
     * Color for use in embed.<br>
     * Arguments (1, 0) and (x, x) mean success.<br>
     * Arguments (0, 1) and (x ∈ [0, y/2), y) mean failure.<br>
     * Arguments (x ∈ [y/2, y), y) mean partial success<br>
     * <h2>Example:</h2>
     * <pre><code>
     *     ctx.reply(ctx.{@link #getEmbedBase() getEmbedBase}()
     *         .setColor(ctx.getColor(5, 8))
     *         .build())
     * </code></pre>
     *
     * @param successCount count of successful operations command did to complete request
     * @param totalCount   total operation count command did to complete request
     * @return {@link java.awt.Color} object
     */
    Color getColor(int successCount, int totalCount);

    /**
     * Error color for use in embed.
     * This method is a shortcut for {@code return getColor(0, 1)}
     *
     * @return {@link java.awt.Color} object
     * @see #getColor(int, int)
     */
    default Color getErrorColor() {
        return getColor(0, 1);
    }

    /**
     * Get footer to set in embed. Will include user (if this is message execution), success and total count of operations.
     * <h2>Example:</h2>
     * <pre><code>
     *     ctx.reply(ctx.{@link #getEmbedBase() getEmbedBase}()
     *         .setFooter(ctx.getFooter(5, 8))
     *         .build())
     * </code></pre>
     *
     * @param successCount count of successful operations command did to complete request
     * @param totalCount   total operation count command did to complete request
     * @return Footer String object
     * @see #getFooter(int, int, String)
     */
    default String getFooter(int successCount, int totalCount) {
        return getFooter(successCount, totalCount, "");
    }

    /**
     * Get footer to set in embed. Will include user (if this is message execution), success and total count of operations.
     * <h2>Example:</h2>
     * <pre><code>
     *     ctx.reply(ctx.{@link #getEmbedBase() getEmbedBase}()
     *         .setFooter(ctx.getFooter(5))
     *         .build())
     * </code></pre>
     *
     * @param successCount count of successful operations command did to complete request
     * @return Footer String object
     * @see #getFooter(int, int, String)
     */
    default String getFooter(int successCount) {
        return getFooter(successCount, 0, "");
    }

    /**
     * Get footer to set in embed. Will include user (if this is message execution), success and total count of operations.
     * <h2>Example:</h2>
     * <pre><code>
     *     ctx.reply(ctx.{@link #getEmbedBase() getEmbedBase}()
     *         .setFooter(ctx.getFooter("Footer text"))
     *         .build())
     * </code></pre>
     *
     * @param textInFooter additional text in footer
     * @return Footer String object
     * @see #getFooter(int, int, String)
     */
    default String getFooter(String textInFooter) {
        return getFooter(1, 0, textInFooter);
    }

    /**
     * Get footer to set in embed. Will include user (if this is message execution), success and total count of operations.
     * <h2>Example:</h2>
     * <pre><code>
     *     ctx.reply(ctx.{@link #getEmbedBase() getEmbedBase}()
     *         .setFooter(ctx.getFooter())
     *         .build())
     * </code></pre>
     *
     * @return Footer String object
     * @see #getFooter(int, int, String)
     */
    default String getFooter() {
        return getFooter(1, 0, "");
    }

    /**
     * Get footer to set in embed. Will include user (if this is message execution), success and total count of operations.
     * <h2>Example:</h2>
     * <pre><code>
     *     ctx.reply(ctx.{@link #getEmbedBase() getEmbedBase}()
     *         .setFooter(ctx.getFooter(5, 8, "Footer text"))
     *         .build())
     * </code></pre>
     *
     * @param successCount count of successful operations command did to complete request
     * @param totalCount   total operation count command did to complete request
     * @param textInFooter additional text in footer
     * @return Footer String object
     */
    String getFooter(int successCount, int totalCount, String textInFooter);

    /**
     * RestAction that you have to flatMap to respond to component interaction.
     * <h2>Example:</h2>
     * <pre><code>
     * ctx.reply(
     *     ctx.{@link #getEmbedBase(String) getEmbedBase}("Response").build(), ActionRow.of(Button.success("button", "Button")))
     *     .flatMap(it -> ctx.getButtonClickEventRestAction())
     *     .flatMap(event -> {} ) // Use {@link ButtonClickEvent} here!
     * </code></pre>
     * <b>You only have 15 minutes to respond to an interaction!</b>
     * @return {@link RestAction RestAction}<{@link ButtonClickEvent}> object that will provide you {@link ButtonClickEvent} or cancel execution of all remaining callbacks
     * @throws NullPointerException if there are no components in your previous {@link #reply(Message) reply}
     */
    RestAction<ButtonClickEvent> getButtonClickEventRestAction();

    /**
     * {@link CompletableFuture} that you have to {@link CompletableFuture#thenCompose(Function) compose} to respond to component interaction<br>
     * <b>You only have 15 minutes to respond to an interaction!</b><br>
     * Do not use that as {@code RestAction.map(it -> ctx.getButtonClickEventCompletableFuture().get()) } as you will get a lot of error messages in logs!<br>
     * This CompletableFuture will be cancelled after 15 minutes of timeout
     * @return {@link CompletableFuture} object with ButtonClickEvent
     * @throws NullPointerException if there are no components in your previous {@link #reply(Message) reply}
     */
    CompletableFuture<ButtonClickEvent> getButtonClickEventCompletableFuture();

    /**
     * Reply with message<br>
     * <h2>Example:</h2>
     * <pre><code>
     *     ctx.reply(new {@link net.dv8tion.jda.api.MessageBuilder MessageBuilder}()
     *         .setContent("Hello, world!")
     *         .build())
     * </code></pre>
     * <b>You only have 15 minutes to respond to an interaction!</b><br>
     * You can include interaction objects right here in the message object instead of using {@link #reply(MessageEmbed, ActionRow...)}
     * @param message {@link Message} that will be sent as response
     * @return {@link RestAction}<{@link Message}> that you have to {@link RestAction#queue() queue}, {@link RestAction#submit() submit} or {@link RestAction#complete() complete} in order to execute this action and send message
     */
    RestAction<Message> reply(Message message);

    /**
     * Reply with embed
     * @param embed {@link MessageEmbed} that will be sent in response message
     * @return {@link RestAction}<{@link Message}> that you have to {@link RestAction#queue() queue}, {@link RestAction#submit() submit} or {@link RestAction#complete() complete} in order to execute this action and send message
     * @see #reply(Message)
     */
    RestAction<Message> reply(MessageEmbed embed);

    /**
     * Reply with embed and components
     * @param embed {@link MessageEmbed} that will be sent in response message
     * @param rows array of {@link ActionRow} with interaction components
     * @return {@link RestAction}<{@link Message}> that you have to {@link RestAction#queue() queue}, {@link RestAction#submit() submit} or {@link RestAction#complete() complete} in order to execute this action and send message
     * @see #reply(Message)
     */
    RestAction<Message> reply(MessageEmbed embed, ActionRow... rows);

    /**
     * Reply with embed contains description<br>
     * This method is a shortCut for {@code reply(getEmbedBase(embedDescription).build())}
     * @param embedDescription description that will be set in embed
     * @return {@link RestAction}<{@link Message}> that you have to {@link RestAction#queue() queue}, {@link RestAction#submit() submit} or {@link RestAction#complete() complete} in order to execute this action and send message
     * @see #reply(Message)
     */
    default RestAction<Message> reply(String embedDescription) {
        return reply(getEmbedBase(embedDescription).build());
    }
    /**
     * Reply with embed contains description and error color<br>
     * This method is a shortCut for {@code reply(getEmbedBase(embedDescription).setColor(getErrorColor()).build())}
     * @param embedDescription description that will be set in embed
     * @return {@link RestAction}<{@link Message}> that you have to {@link RestAction#queue() queue}, {@link RestAction#submit() submit} or {@link RestAction#complete() complete} in order to execute this action and send message
     * @see #reply(Message)
     */
    default RestAction<Message> replyError(String embedDescription) {
        return reply(getEmbedBase(embedDescription).setColor(getErrorColor()).build());
    }

    /**
     * Reply with embed and components, then flatMap to {@link ButtonClickEvent}
     * This method is a shortcut for {@code reply(embed, rows).flatMap(it -> getButtonClickEventRestAction())}
     * @param embed embed that will be sent in response message
     * @param rows array of {@link ActionRow actions rows} with interaction components
     * @return {@link RestAction}<{@link Message}> that you have to {@link RestAction#queue() queue}, {@link RestAction#submit() submit} or {@link RestAction#complete() complete} in order to execute this action and send message
     * @see #reply(Message)
     */
    default RestAction<ButtonClickEvent> replyThenWaitUserClick(MessageEmbed embed, ActionRow... rows) {
        return reply(embed, rows).flatMap(it -> getButtonClickEventRestAction());
    }

    /**
     * Reply with message describing exception details<br>
     * You can also use it like this example:
     * <pre><code>
     * ctx -> ctx
     *     .{@link #replyThenWaitUserClick(MessageEmbed, ActionRow... rows) replyThenWaitUserClick}(ctx.{@link #getEmbedBase() getEmbedBase}()
     *             .setDescription("Description)
     *             .build(),
     *         ActionRow.of(Button.primary("button", "Example button 1")))
     *     .flatMap(event -> { ... })
     *     // ..other calls, if any
     *     .queue(null, ctx::replyException)
     * </code></pre>
     * Do all checks synchronously because .queue will not pass through any exceptions in callbacks (such as flatMap or map)
     */
    void replyException(Throwable t);
}
