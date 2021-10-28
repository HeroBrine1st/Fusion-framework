package ru.herobrine1st.fusion.api.command;

import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Incubating
public interface ReactiveContext {
    /**
     * Acknowledge this interaction and defer the reply to a later time.<br>
     * You can use {@code deferReply()} or {@code deferReply(false)} to send a non-ephemeral deferred reply.<br>
     * This method is created for reactive commands. It means that with {@link ComponentInteraction} event this method will actually do {@link ComponentInteraction#deferEdit()}. It's a convenience for you, the developer.<br>
     * This method should not be used in {@link RestAction} callbacks.
     * @param ephemeral True, if this message should only be visible to the interaction user
     * @return {@link InteractionCallbackAction}
     * @see Interaction#deferReply(boolean)
     */
    InteractionCallbackAction deferReply(boolean ephemeral);

    /**
     * Acknowledge this interaction and defer the reply to a later time.<br>
     * You can use {@code deferReply(true)} to send a deferred ephemeral reply.
     *
     * @return {@link InteractionCallbackAction}
     * @see Interaction#deferReply(boolean)
     */
    default InteractionCallbackAction deferReply() {
        return deferReply(false);
    }

    /**
     * Wait for any component interaction in your message and re-execute in this context with new event.
     *
     * @param message      message with component that you sent and got back with {@link RestAction}'s callback
     * @param validateUser True, if only author of command could interact.
     * @return The same message object
     */
    Message submitComponents(Message message, boolean validateUser);

    /**
     * Wait for any component interaction in your message and re-execute in this context with new event.<br>
     * Only author of command can interact with components.
     *
     * @param message message with components that you sent and got back with {@link RestAction}'s callback
     * @return The same message object
     * @see #submitComponents(Message, boolean)
     */
    default Message submitComponents(Message message) {
        return submitComponents(message, true);
    }

    /**
     * Create a reactive state that will change every time user interact with (defined with id) components<br>
     * <br>
     * <b>You should run reactive methods unconditionally</b>, otherwise you get occur exceptions because {@code <T>} is used in <b>unsafe cast</b>
     * @param initialState Initial value of this state
     * @param onComponentInteraction Callback that receive component id and old value, and returns new value
     * @param componentIds Array of component ids which {@code onComponentInteraction} callback should react on
     * @param <T> Type of state's value
     * @return Current value of state
     */
    <T> T useState(T initialState, BiFunction<String, T, T> onComponentInteraction, String... componentIds);

    /**
     * Cache result of asynchronous computation.
     * If there's cached result, you will get a completed {@link java.util.concurrent.CompletableFuture} with cached value.<br>
     * <br>
     * <b>You should run reactive methods unconditionally</b>, otherwise you will get exceptions because {@code <T>} is used in <b>unsafe cast</b><br>
     * <b>You should wait for all asynchronous computations before sending a message</b>
     * @param completableFutureSupplier supplier that returns a {@code CompletableFuture} that returns value to cache. Called only when need get or update cached value.
     * @param dependencies Optional objects. If any of them changed, {@code completableFutureSuppluer} will be called, and you will get copy of supplied {@code CompletableFuture}
     * @param <T> type of value
     * @return {@link CompletableFuture}
     */
    <T> CompletableFuture<T> useEffect(Supplier<CompletableFuture<T>> completableFutureSupplier, Object... dependencies);
}
