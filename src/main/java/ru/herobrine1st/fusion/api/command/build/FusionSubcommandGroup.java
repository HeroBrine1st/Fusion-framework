package ru.herobrine1st.fusion.api.command.build;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.FusionOptionData;
import ru.herobrine1st.fusion.api.command.PermissionHandler;

import java.util.ArrayList;
import java.util.List;

public final class FusionSubcommandGroup extends FusionOptionData {
    private final List<FusionSubcommand> subcommandData = new ArrayList<>();
    private PermissionHandler permissionHandler = PermissionHandler.DEFAULT;

    public FusionSubcommandGroup(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    public List<FusionSubcommand> getSubcommandData() {
        return subcommandData;
    }

    public FusionSubcommandGroup addSubcommands(FusionSubcommand... data) {
        Checks.check(data.length + subcommandData.size() <= 25, "Cannot have more than 25 subcommands for a subcommand group!");
        subcommandData.addAll(List.of(data));
        return this;
    }

    @NotNull
    public FusionSubcommandGroup setPermissionHandler(PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
        return this;
    }

    @NotNull
    public PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }
}
