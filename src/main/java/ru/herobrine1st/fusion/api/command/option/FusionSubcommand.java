package ru.herobrine1st.fusion.api.command.option;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandExecutor;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.build.SubcommandBuilder;
import ru.herobrine1st.fusion.api.command.option.parser.ParserElement;

import java.util.List;

public final class FusionSubcommand extends FusionBaseCommand<ParserElement<?, ?>> {
    public FusionSubcommand(@NotNull String name, @NotNull String description, @NotNull CommandExecutor executor,
                            @NotNull List<ParserElement<?, ?>> options,
                            @NotNull PermissionHandler permissionHandler) {
        super(name, description, executor, options, permissionHandler);
        Checks.check(
                options.stream()
                        .dropWhile(it -> it.getOptionData().isRequired())
                        .noneMatch(it -> it.getOptionData().isRequired()),
                "You should add non-required arguments after required ones");
        Checks.notNull(executor, "Executor");
    }

    public static SubcommandBuilder builder(@NotNull String name, @NotNull String description) {
        return new SubcommandBuilder(name, description);
    }
}
