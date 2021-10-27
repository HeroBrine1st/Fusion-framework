package ru.herobrine1st.fusion.api.command.build;

import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.option.FusionCommand;
import ru.herobrine1st.fusion.api.command.option.FusionSubcommandGroup;

public final class WithSubcommandGroupsBuilder extends CommandBuilder<WithSubcommandGroupsBuilder, FusionSubcommandGroup> {
    public WithSubcommandGroupsBuilder(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    @Override
    public FusionCommand<FusionSubcommandGroup> build() {
        return new FusionCommand.WithSubcommandGroups(name, description, options, permissionHandler);
    }
}
