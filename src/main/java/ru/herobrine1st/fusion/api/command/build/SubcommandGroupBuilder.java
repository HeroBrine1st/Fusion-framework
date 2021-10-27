package ru.herobrine1st.fusion.api.command.build;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.option.FusionSubcommand;
import ru.herobrine1st.fusion.api.command.option.FusionSubcommandGroup;

import java.util.ArrayList;
import java.util.List;

public final class SubcommandGroupBuilder extends OptionBuilder<SubcommandGroupBuilder> {
    private PermissionHandler permissionHandler = PermissionHandler.DEFAULT;
    private final List<FusionSubcommand> subcommandData = new ArrayList<>();

    public SubcommandGroupBuilder(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    public SubcommandGroupBuilder addSubcommands(FusionSubcommand... data) {
        Checks.check(data.length + subcommandData.size() <= 25, "Cannot have more than 25 subcommands for a subcommand group!");
        subcommandData.addAll(List.of(data));
        return this;
    }

    public SubcommandGroupBuilder setPermissionHandler(PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
        return this;
    }

    @Override
    public FusionSubcommandGroup build() {
        return new FusionSubcommandGroup(name, description, permissionHandler, subcommandData);
    }

}
