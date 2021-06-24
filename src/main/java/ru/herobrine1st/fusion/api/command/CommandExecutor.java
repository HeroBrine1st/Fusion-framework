package ru.herobrine1st.fusion.api.command;

import net.dv8tion.jda.api.requests.RestAction;
import ru.herobrine1st.fusion.api.exception.CommandException;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CommandExecutor {
    void execute(@NotNull CommandContext ctx) throws CommandException;
}
