package ru.herobrine1st.fusion.api.command;

import ru.herobrine1st.fusion.api.exception.CommandException;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CommandExecutor {
    void execute(@NotNull CommandContext ctx) throws CommandException;
}
