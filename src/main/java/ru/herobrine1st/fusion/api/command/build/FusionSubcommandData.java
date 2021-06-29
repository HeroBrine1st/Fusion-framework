package ru.herobrine1st.fusion.api.command.build;

import javax.annotation.Nonnull;

public class FusionSubcommandData extends FusionBaseCommand<FusionSubcommandData> {
    public FusionSubcommandData(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }
}
