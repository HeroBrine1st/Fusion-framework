package ru.herobrine1st.fusion.api.command.build;

import ru.herobrine1st.fusion.api.command.args.parser.ParserElement;

import javax.annotation.Nonnull;

public class FusionSubcommand extends FusionBaseCommand<FusionSubcommand, ParserElement<?, ?>> {
    public FusionSubcommand(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }
}
