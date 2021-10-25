package ru.herobrine1st.fusion.api.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.herobrine1st.fusion.api.command.args.parser.ParserElement;
import ru.herobrine1st.fusion.api.command.build.SubcommandBuilder;

import java.util.List;

public final class FusionSubcommand extends FusionBaseCommand<ParserElement<?, ?>> {
    public FusionSubcommand(@NotNull String name, @NotNull String description, @Nullable CommandExecutor executor,
                            @NotNull List<ParserElement<?, ?>> options, @NotNull String shortName,
                            @NotNull PermissionHandler permissionHandler) {
        super(name, description, executor, options, shortName, permissionHandler);
    }

    public static SubcommandBuilder builder(@NotNull String name, @NotNull String description) {
        return new SubcommandBuilder(name, description);
    }
}
