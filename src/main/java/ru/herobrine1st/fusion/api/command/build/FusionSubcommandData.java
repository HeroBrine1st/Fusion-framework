package ru.herobrine1st.fusion.api.command.build;

import ru.herobrine1st.fusion.api.command.args.ParserElement;

import javax.annotation.Nonnull;

public class FusionSubcommandData extends FusionBaseCommand<FusionSubcommandData, ParserElement> {
    public FusionSubcommandData(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }
}
