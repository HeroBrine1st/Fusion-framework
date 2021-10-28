package ru.herobrine1st.fusion.api.command;

import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import ru.herobrine1st.fusion.api.command.option.FusionBaseCommand;
import ru.herobrine1st.fusion.api.exception.CommandException;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A class representing context of command execution.<br>
 * This is used to get various  execution details and reply independent of event that triggered the execution.<br>
 * <br>
 * Methods that return an instance of {@link RestAction} require an additional step to complete the execution.
 * Learn more {@link RestAction here}<br>
 * <br>
 * <h2>Example of a simple Hello, user! command:</h2>
 * <pre><code>
 *     class HelloUserCommand implements {@link CommandExecutor} {
 *         public void execute(CommandContext ctx) throws {@link CommandException} {
 *             ctx.{@link #getEvent() getEvent}().{@link GenericInteractionCreateEvent#reply(String) reply}("Hello, " + ctx.{@link #getUser() getUser}().getName() + "!").queue();
 *         }
 *     }
 * </code></pre>
 * All methods in this class return nonnull values unless annotated with @{@link org.jetbrains.annotations.Nullable Nullable}<br>
 */
public interface CommandContext {
    /**
     * Event that triggered command this command execution.
     *
     * @return {@link Event} object. Instance of either {@link SlashCommandEvent} or {@link ButtonClickEvent}
     */
    GenericInteractionCreateEvent getEvent();

    /**
     * Interaction hook
     *
     * @return {@link InteractionHook} instance
     */
    InteractionHook getHook();

    /**
     * Acknowledge this interaction and defer the reply to a later time.<br>
     * You can use {@code deferReply()} or {@code deferReply(false)} to send a non-ephemeral deferred reply.
     * @param ephemeral True, if this message should only be visible to the interaction user
     * @return {@link InteractionCallbackAction}
     * @see Interaction#deferReply(boolean)
     */
    InteractionCallbackAction deferReply(boolean ephemeral);

    /**
     * Acknowledge this interaction and defer the reply to a later time.<br>
     * You can use {@code deferReply(true)} to send a deferred ephemeral reply.
     * @return {@link InteractionCallbackAction}
     * @see Interaction#deferReply(boolean)
     */
    default InteractionCallbackAction deferReply() {
        return deferReply(false);
    }

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
     * <b>Rarely used inside {@link CommandExecutor}, do not use if you don't know what it is!</b>
     *
     * @param name  name of the argument
     * @param value value of the argument
     */
    void putArg(String name, Object value);

    /**
     * Get argument with certain name from context
     *
     * @param name name of the argument
     * @param <T>  type of the argument. Will be used in <b>unsafe cast</b>.
     * @return {@link Optional}<T> container with argument
     * @see #getArguments(String)
     */
    <T> Optional<T> getArgument(String name);

    /**
     * Get collection of arguments with certain name from context
     *
     * @param name name of the argument
     * @param <T>  type of the argument. Will be used in <b>unsafe cast</b>.
     * @return {@link Collection}<T> of arguments. Empty if no arguments.
     * @see #getArgument(String)
     */
    <T> Collection<T> getArguments(String name);

    /**
     * User that triggered this execution
     *
     * @return {@link User} object
     */
    User getUser();

    /**
     * Command executing in this context
     *
     * @return {@link FusionBaseCommand}<?> object describing command that (usually) called this method.
     */
    FusionBaseCommand<?> getCommand();

    /**
     * CompletableFuture that will be completed when user clicked any button in your message.
     * Will be cancelled after 15 minutes.
     *
     * @param message message with commands that you sent and got back with {@link RestAction}'s callback
     * @return {@link CompletableFuture}<{@link ButtonClickEvent}> instance
     * @see ru.herobrine1st.fusion.api.restaction.CompletableFutureRestAction
     */
    CompletableFuture<ButtonClickEvent> waitForComponentInteraction(Message message);

    /**
     * Wait for any component interaction in your message and re-execute in this context with new event
     * @param message message with commands that you sent and got back with {@link RestAction}'s callback
     * @return The same message object
     */
    @Incubating
    Message submitComponents(Message message);
}
