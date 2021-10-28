package ru.herobrine1st.fusion.api.command.option;

import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.PermissionHandler;

import java.util.List;

public final class FusionCommandWithSubcommandGroups extends FusionCommand<FusionSubcommandGroup> {
    public FusionCommandWithSubcommandGroups(@NotNull String name, @NotNull String description,
                                             @NotNull List<FusionSubcommandGroup> options,
                                             @NotNull PermissionHandler permissionHandler) {
        super(name, description, null, options, permissionHandler);
    }

}
