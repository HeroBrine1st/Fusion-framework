package ru.herobrine1st.fusion.api.command.build;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.FusionCommand;
import ru.herobrine1st.fusion.api.command.FusionSubcommand;

public final class WithSubcommandsBuilder extends CommandBuilder<WithSubcommandsBuilder, FusionSubcommand> {
    public WithSubcommandsBuilder(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    @Override
    public FusionCommand<FusionSubcommand> build() {
        Checks.check(this.options.size() <= 25, "Cannot have more than 25 options for a command!");
        return new FusionCommand.WithSubcommands(name, description, options, shortName, permissionHandler, testing);
    }
}
