package ru.herobrine1st.fusion.api.command.build;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.FusionOptionData;
import ru.herobrine1st.fusion.api.command.PermissionHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FusionSubcommandGroupData extends FusionOptionData {
    private final List<FusionSubcommandData> subcommandData = new ArrayList<>();
    private PermissionHandler permissionHandler = PermissionHandler.DEFAULT;

    public FusionSubcommandGroupData(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    public List<FusionSubcommandData> getSubcommandData() {
        return subcommandData;
    }

    public FusionSubcommandGroupData addSubcommands(FusionSubcommandData... data) {
        Checks.check(data.length + subcommandData.size() <= 25, "Cannot have more than 25 subcommands for a subcommand group!");
        subcommandData.addAll(List.of(data));
        return this;
    }

    @Nonnull
    public FusionSubcommandGroupData setPermissionHandler(PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
        return this;
    }

    @Nonnull
    public PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }
}
