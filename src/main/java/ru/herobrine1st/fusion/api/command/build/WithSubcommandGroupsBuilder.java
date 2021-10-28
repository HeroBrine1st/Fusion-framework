package ru.herobrine1st.fusion.api.command.build;

import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.option.FusionCommand;
import ru.herobrine1st.fusion.api.command.option.FusionCommandWithSubcommandGroups;
import ru.herobrine1st.fusion.api.command.option.FusionSubcommandGroup;

public final class WithSubcommandGroupsBuilder extends BaseCommandBuilder<WithSubcommandGroupsBuilder, FusionSubcommandGroup> {
    public WithSubcommandGroupsBuilder(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    @Override
    public FusionCommand<FusionSubcommandGroup> build() {
        return new FusionCommandWithSubcommandGroups(name, description, options, permissionHandler);
    }
}
