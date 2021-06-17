package ru.herobrine1st.fusion.api.command;

import ru.herobrine1st.fusion.api.exception.CommandException;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CommandExecutor {
    @NotNull CommandResult execute(@NotNull CommandContext ctx) throws CommandException;
}
