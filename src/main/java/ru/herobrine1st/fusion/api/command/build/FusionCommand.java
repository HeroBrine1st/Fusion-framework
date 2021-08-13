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
    public static @NotNull FusionCommand<ParserElement<?, ?>> withArguments(@NotNull String name, @NotNull String description) {
        return new FusionCommand<>(name, description);
    }

    @Contract("_, _ -> new")
    public static @NotNull FusionCommand<FusionSubcommand> withSubcommands(@NotNull String name, @NotNull String description) {
        return new FusionCommand<>(name, description);
    }

    @Contract("_, _ -> new")
    public static @NotNull FusionCommand<FusionSubcommandGroup> withSubcommandGroups(@NotNull String name, @NotNull String description) {
        return new FusionCommand<>(name, description);
    }
}