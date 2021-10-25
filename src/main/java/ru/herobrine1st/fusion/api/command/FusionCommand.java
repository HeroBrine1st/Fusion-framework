package ru.herobrine1st.fusion.api.command;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.herobrine1st.fusion.api.command.args.parser.ParserElement;
import ru.herobrine1st.fusion.api.command.build.WithArgumentsBuilder;
import ru.herobrine1st.fusion.api.command.build.WithSubcommandGroupsBuilder;
import ru.herobrine1st.fusion.api.command.build.WithSubcommandsBuilder;

import java.util.List;

public sealed class FusionCommand<R extends FusionOptionData> extends FusionBaseCommand<R> {
    private final boolean testing;

    private FusionCommand(@NotNull String name, @NotNull String description, @Nullable CommandExecutor executor,
                          @NotNull List<R> options, @NotNull String shortName,
                          @NotNull PermissionHandler permissionHandler, boolean testing) {
        super(name, description, executor, options, shortName, permissionHandler);
        this.testing = testing;
    }

    public boolean isTesting() {
        return testing;
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

    public static final class WithArguments extends FusionCommand<ParserElement<?, ?>> {
        public WithArguments(@NotNull String name, @NotNull String description, @Nullable CommandExecutor executor,
                             @NotNull List<ParserElement<?, ?>> options, @NotNull String shortName,
                             @NotNull PermissionHandler permissionHandler, boolean testing) {
            super(name, description, executor, options, shortName, permissionHandler, testing);
        }
    }

    public static final class WithSubcommands extends FusionCommand<FusionSubcommand> {
        public WithSubcommands(@NotNull String name, @NotNull String description,
                               @NotNull List<FusionSubcommand> options, @NotNull String shortName,
                               @NotNull PermissionHandler permissionHandler, boolean testing) {
            super(name, description, null, options, shortName, permissionHandler, testing);
        }

    }

    public static final class WithSubcommandGroups extends FusionCommand<FusionSubcommandGroup> {
        public WithSubcommandGroups(@NotNull String name, @NotNull String description,
                                    @NotNull List<FusionSubcommandGroup> options, @NotNull String shortName,
                                    @NotNull PermissionHandler permissionHandler, boolean testing) {
            super(name, description, null, options, shortName, permissionHandler, testing);
        }

    }

}