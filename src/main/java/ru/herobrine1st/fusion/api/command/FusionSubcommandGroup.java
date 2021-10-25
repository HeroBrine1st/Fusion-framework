package ru.herobrine1st.fusion.api.command;

import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.build.SubcommandGroupBuilder;

import java.util.List;

public final class FusionSubcommandGroup extends FusionOptionData {
    private final List<FusionSubcommand> subcommandData;
    private final PermissionHandler permissionHandler;

    public FusionSubcommandGroup(@NotNull String name, @NotNull String description,
                                 @NotNull PermissionHandler permissionHandler, @NotNull List<FusionSubcommand> subcommandData) {
        super(name, description);
        this.permissionHandler = permissionHandler;
        this.subcommandData = subcommandData;
    }

    public List<FusionSubcommand> getSubcommandData() {
        return subcommandData;
    }

    @NotNull
    public PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }

    public static SubcommandGroupBuilder builder(@NotNull String name, @NotNull String description) {
        return new SubcommandGroupBuilder(name, description);
    }

}
