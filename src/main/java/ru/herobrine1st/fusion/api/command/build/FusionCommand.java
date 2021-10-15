package ru.herobrine1st.fusion.api.command.build;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.FusionOptionData;
import ru.herobrine1st.fusion.api.command.args.parser.ParserElement;

public class FusionCommand<R extends FusionOptionData> extends FusionBaseCommand<FusionCommand<R>, R> {
    private FusionCommand(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    @Contract("_, _ -> new")
    public static @NotNull WithArguments withArguments(@NotNull String name, @NotNull String description) {
        return new WithArguments(name, description);
    }

    @Contract("_, _ -> new")
    public static @NotNull WithSubcommands withSubcommands(@NotNull String name, @NotNull String description) {
        return new WithSubcommands(name, description);
    }

    @Contract("_, _ -> new")
    public static @NotNull WithSubcommandGroups withSubcommandGroups(@NotNull String name, @NotNull String description) {
        return new WithSubcommandGroups(name, description);
    }

    public static class WithArguments extends FusionCommand<ParserElement<?, ?>> {
        private WithArguments(@NotNull String name, @NotNull String description) {
            super(name, description);
        }
    }

    public static class WithSubcommands extends FusionCommand<FusionSubcommand> {
        private WithSubcommands(@NotNull String name, @NotNull String description) {
            super(name, description);
        }
    }

    public static class WithSubcommandGroups extends FusionCommand<FusionSubcommandGroup> {
        private WithSubcommandGroups(@NotNull String name, @NotNull String description) {
            super(name, description);
        }
    }
}