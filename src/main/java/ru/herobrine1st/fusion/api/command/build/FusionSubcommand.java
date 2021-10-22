package ru.herobrine1st.fusion.api.command.build;

import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.args.parser.ParserElement;

public final class FusionSubcommand extends FusionBaseCommand<FusionSubcommand, ParserElement<?, ?>> {
    public FusionSubcommand(@NotNull String name, @NotNull String description) {
        super(name, description);
    }
}
