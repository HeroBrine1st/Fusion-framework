package ru.herobrine1st.fusion.api.command.build;

import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.option.FusionOptionData;

public abstract sealed class CommandBuilder<T extends CommandBuilder<T, R>, R extends FusionOptionData>
        extends BaseCommandBuilder<T, R>
        permits WithArgumentsBuilder, WithSubcommandGroupsBuilder, WithSubcommandsBuilder {
    protected boolean testing = false;

    protected CommandBuilder(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    public T setTesting(boolean testing) {
        this.testing = testing;
        //noinspection unchecked
        return (T) this;
    }
}
