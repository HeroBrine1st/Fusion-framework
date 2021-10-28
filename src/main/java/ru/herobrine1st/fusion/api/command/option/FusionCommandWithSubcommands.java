package ru.herobrine1st.fusion.api.command.option;

import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.PermissionHandler;

import java.util.List;

public final class FusionCommandWithSubcommands extends FusionCommand<FusionSubcommand> {
    public FusionCommandWithSubcommands(@NotNull String name, @NotNull String description,
                                        @NotNull List<FusionSubcommand> options,
                                        @NotNull PermissionHandler permissionHandler) {
        super(name, description, null, options, permissionHandler);
    }

}
