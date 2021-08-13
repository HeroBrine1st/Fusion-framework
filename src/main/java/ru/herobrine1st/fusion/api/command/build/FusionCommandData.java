package ru.herobrine1st.fusion.api.command.build;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.FusionOptionData;
import ru.herobrine1st.fusion.api.command.args.parser.ParserElement;

public class FusionCommandData<R extends FusionOptionData> extends FusionBaseCommand<FusionCommandData<R>, R> {
    private FusionCommandData(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    @Contract("_, _ -> new")
    public static @NotNull FusionCommandData<ParserElement<?, ?>> withArguments(@NotNull String name, @NotNull String description) {
        return new FusionCommandData<>(name, description);
    }

    @Contract("_, _ -> new")
    public static @NotNull FusionCommandData<FusionSubcommandData> withSubcommands(@NotNull String name, @NotNull String description) {
        return new FusionCommandData<>(name, description);
    }

    @Contract("_, _ -> new")
    public static @NotNull FusionCommandData<FusionSubcommandGroupData> withSubcommandGroups(@NotNull String name, @NotNull String description) {
        return new FusionCommandData<>(name, description);
    }
}