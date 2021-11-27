package ru.herobrine1st.fusion.api.manager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.option.FusionCommand;
import ru.herobrine1st.fusion.api.exception.ExceptionHandler;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;

import javax.annotation.CheckReturnValue;
import java.util.Collection;

public interface CommandManager {

    @Contract("_ -> new")
    static @NotNull CommandManager create(JDA jda) {
        return new CommandManagerImpl(jda);
    }

    void registerCommand(FusionCommand<?> data);

    /**
     * Sends the complete list of global commands.<br>
     * This will replace the existing command list for bot. You should only use this once on startup!
     * @return {@link CommandListUpdateAction}
     */
    @CheckReturnValue
    CommandListUpdateAction updateCommands();

    /**
     * Sends the complete list of guild commands.<br>
     * This will replace the existing command list for guild. You should only use this once on startup!
     * @param guild guild that command list will be sent to.
     * @return {@link CommandListUpdateAction}
     */
    @CheckReturnValue
    CommandListUpdateAction updateCommands(@NotNull Guild guild);

    /**
     * @return List of CommandData
     */
    Collection<CommandData> getCommandData();

    void registerListeners();

    void setExceptionHandler(ExceptionHandler exceptionHandler);
}
