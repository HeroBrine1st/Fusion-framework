package ru.herobrine1st.fusion.api.command.build;

import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.option.FusionCommand;
import ru.herobrine1st.fusion.api.command.option.FusionSubcommand;

public final class WithSubcommandsBuilder extends BaseCommandBuilder<WithSubcommandsBuilder, FusionSubcommand> {
    public WithSubcommandsBuilder(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    @Override
    public FusionCommand<FusionSubcommand> build() {
        return new FusionCommand.WithSubcommands(name, description, options, permissionHandler);
    }
}
