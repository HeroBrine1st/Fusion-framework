package ru.herobrine1st.fusion.api.command.build;

import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.FusionOptionData;

public abstract sealed class OptionBuilder<T extends OptionBuilder<T>> permits BaseCommandBuilder, SubcommandGroupBuilder {
    protected final String name;
    protected final String description;

    public OptionBuilder(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }

    public abstract FusionOptionData build();
}
