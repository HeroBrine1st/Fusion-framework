package ru.herobrine1st.fusion.api.exception;

import ru.herobrine1st.fusion.api.command.CommandContext;

@FunctionalInterface
public interface ExceptionHandler {
    /**
     * Handler that sends a message
     * @param ctx Context exception happened in
     * @param exception may be either {@link CommandException} or {@link RuntimeException}
     */
    void handle(CommandContext ctx, RuntimeException exception);
}
