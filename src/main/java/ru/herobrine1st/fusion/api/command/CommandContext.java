package ru.herobrine1st.fusion.api.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;
import ru.herobrine1st.fusion.api.command.option.FusionBaseCommand;
import ru.herobrine1st.fusion.api.exception.CommandException;
import ru.herobrine1st.fusion.api.restaction.CompletableFutureAction;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * An interface representing context of command execution.<br>
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
public interface CommandContext extends ReactiveContext {
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
     * @param message message with components that you sent and got back with {@link RestAction}'s callback
     * @param validateUser True, if only author of command could interact.
     * @return {@link CompletableFuture}<{@link ButtonClickEvent}> instance
     * @see CompletableFutureAction
     */
    CompletableFuture<GenericComponentInteractionCreateEvent> waitForComponentInteraction(Message message, boolean validateUser);

    /**
     * CompletableFuture that will be completed when user clicked any button in your message.
     * Will be cancelled after 15 minutes.<br>
     * Only author of command can interact with components.
     * @param message message with components that you sent and got back with {@link RestAction}'s callback
     * @return {@link CompletableFuture}<{@link ButtonClickEvent}> instance
     * @see CompletableFutureAction
     * @see #waitForComponentInteraction(Message, boolean)
     */
    default CompletableFuture<GenericComponentInteractionCreateEvent> waitForComponentInteraction(Message message) {
        return waitForComponentInteraction(message, true);
    }
}
