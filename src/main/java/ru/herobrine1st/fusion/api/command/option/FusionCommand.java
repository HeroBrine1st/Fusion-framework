package ru.herobrine1st.fusion.api.command.option;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.herobrine1st.fusion.api.command.CommandExecutor;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.build.WithArgumentsBuilder;
import ru.herobrine1st.fusion.api.command.build.WithSubcommandGroupsBuilder;
import ru.herobrine1st.fusion.api.command.build.WithSubcommandsBuilder;

import java.util.List;

public /*sealed*/ class FusionCommand<R extends FusionOptionData>
        extends FusionBaseCommand<R>
        /*permits FusionCommandWithArguments, FusionCommandWithSubcommandGroups, FusionCommandWithSubcommands*/ {

    public FusionCommand(@NotNull String name, @NotNull String description, @Nullable CommandExecutor executor,
                          @NotNull List<R> options,
                          @NotNull PermissionHandler permissionHandler) {
        super(name, description, executor, options, permissionHandler);
    }

    @Contract("_, _ -> new")
    public static @NotNull WithArgumentsBuilder withArguments(@NotNull String name, @NotNull String description) {
        return new WithArgumentsBuilder(name, description);
    }

    @Contract("_, _ -> new")
    public static @NotNull WithSubcommandsBuilder withSubcommands(@NotNull String name, @NotNull String description) {
        return new WithSubcommandsBuilder(name, description);
    }

    @Contract("_, _ -> new")
    public static @NotNull WithSubcommandGroupsBuilder withSubcommandGroups(@NotNull String name, @NotNull String description) {
        return new WithSubcommandGroupsBuilder(name, description);
    }

}