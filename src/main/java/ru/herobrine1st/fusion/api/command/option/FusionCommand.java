package ru.herobrine1st.fusion.api.command.option;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.herobrine1st.fusion.api.command.CommandExecutor;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.build.WithArgumentsBuilder;
import ru.herobrine1st.fusion.api.command.build.WithSubcommandGroupsBuilder;
import ru.herobrine1st.fusion.api.command.build.WithSubcommandsBuilder;
import ru.herobrine1st.fusion.api.command.option.parser.ParserElement;

import java.util.List;

public sealed class FusionCommand<R extends FusionOptionData> extends FusionBaseCommand<R> {

    private FusionCommand(@NotNull String name, @NotNull String description, @Nullable CommandExecutor executor,
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

    public static final class WithArguments extends FusionCommand<ParserElement<?, ?>> {
        public WithArguments(@NotNull String name, @NotNull String description, @NotNull CommandExecutor executor,
                             @NotNull List<ParserElement<?, ?>> options,
                             @NotNull PermissionHandler permissionHandler) {
            super(name, description, executor, options, permissionHandler);
            Checks.check(
                    options.stream()
                            .dropWhile(it -> it.getOptionData().isRequired())
                            .noneMatch(it -> it.getOptionData().isRequired()),
                    "You should add non-required arguments after required ones");
            Checks.notNull(executor, "Executor");
        }
    }

    public static final class WithSubcommands extends FusionCommand<FusionSubcommand> {
        public WithSubcommands(@NotNull String name, @NotNull String description,
                               @NotNull List<FusionSubcommand> options,
                               @NotNull PermissionHandler permissionHandler) {
            super(name, description, null, options, permissionHandler);
        }

    }

    public static final class WithSubcommandGroups extends FusionCommand<FusionSubcommandGroup> {
        public WithSubcommandGroups(@NotNull String name, @NotNull String description,
                                    @NotNull List<FusionSubcommandGroup> options,
                                    @NotNull PermissionHandler permissionHandler) {
            super(name, description, null, options, permissionHandler);
        }

    }

}