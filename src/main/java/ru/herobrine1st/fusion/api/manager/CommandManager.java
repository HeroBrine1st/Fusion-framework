package ru.herobrine1st.fusion.api.manager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.build.FusionCommand;
import ru.herobrine1st.fusion.api.exception.ExceptionHandler;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;

import java.util.List;

public interface CommandManager {

    @Contract("_ -> new")
    static @NotNull CommandManager create(JDA jda) {
        return new CommandManagerImpl(jda);
    }

    void registerCommand(FusionCommand<?> data);

    List<FusionCommand<?>> getCommands();

    void updateCommands(Guild testingGuild);

    default void updateCommands() {
        updateCommands(null);
    }

    void registerListeners();

    void setExceptionHandler(ExceptionHandler exceptionHandler);
}
