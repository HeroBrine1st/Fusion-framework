package ru.herobrine1st.fusion.api.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract sealed class FusionBaseCommand<R extends FusionOptionData> extends FusionOptionData permits FusionCommand, FusionSubcommand {
    private final CommandExecutor executor;
    private final List<R> options;
    private final String shortName;
    private final PermissionHandler permissionHandler;



    protected FusionBaseCommand(@NotNull String name, @NotNull String description,
                                @Nullable CommandExecutor executor, @NotNull List<R> options,
                                @NotNull String shortName, @NotNull PermissionHandler permissionHandler) {
        super(name, description);
        this.executor = executor;
        this.options = options;
        this.shortName = shortName;
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
        return executor;
    }

    @NotNull
    public String getShortName() {
        return shortName;
    }

}
