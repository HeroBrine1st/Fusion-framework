package ru.herobrine1st.fusion.api.command;

import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.exception.CommandException;

@FunctionalInterface
public interface CommandExecutor {
    void execute(@NotNull CommandContext ctx) throws CommandException;
}
