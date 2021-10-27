package ru.herobrine1st.fusion.api.command.option;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.herobrine1st.fusion.api.command.CommandExecutor;
import ru.herobrine1st.fusion.api.command.PermissionHandler;

import java.util.List;

public abstract sealed class FusionBaseCommand<R extends FusionOptionData> extends FusionOptionData permits FusionCommand, FusionSubcommand {
    private final CommandExecutor executor;
    private final List<R> options;
    private final PermissionHandler permissionHandler;



    protected FusionBaseCommand(@NotNull String name, @NotNull String description,
                                @Nullable CommandExecutor executor, @NotNull List<R> options,
                                @NotNull PermissionHandler permissionHandler) {
        super(name, description);
        Checks.check(options.size() <= 25, "Cannot have more than 25 options for a command!");
        this.executor = executor;
        this.options = options;
        this.permissionHandler = permissionHandler;
    }

    @NotNull
    public List<R> getOptions() {
        return options;
    }

    @NotNull
    public PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }

    @NotNull
    public CommandExecutor getExecutor() {
        if(executor == null) // If there's no executor, there are subcommands with executor, so there's always executor in tree hierarchy
            throw new IllegalStateException("Executor is null");
        return executor;
    }
}
