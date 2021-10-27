package ru.herobrine1st.fusion.api.command.build;

import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandExecutor;
import ru.herobrine1st.fusion.api.command.option.FusionCommand;
import ru.herobrine1st.fusion.api.command.option.parser.ParserElement;

public final class WithArgumentsBuilder extends BaseCommandBuilder<WithArgumentsBuilder, ParserElement<?, ?>> {
    private CommandExecutor executor = null;

    public WithArgumentsBuilder(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    public WithArgumentsBuilder setExecutor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public FusionCommand<ParserElement<?, ?>> build() {
        return new FusionCommand.WithArguments(name, description, executor, options, permissionHandler);
    }
}
