package ru.herobrine1st.fusion.api.command;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.args.parser.ParserElement;
import ru.herobrine1st.fusion.api.command.build.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandGroup;

public abstract sealed class FusionOptionData permits FusionBaseCommand, FusionSubcommandGroup, ParserElement {
    protected String name, description;

    public FusionOptionData(@NotNull String name, @NotNull String description) {
        setName(name);
        setDescription(description);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    public void setName(@NotNull String name) {
        Checks.notEmpty(name, "Name");
        Checks.notLonger(name, 32, "Name");
        Checks.isLowercase(name, "Name");
        Checks.matches(name, Checks.ALPHANUMERIC_WITH_DASH, "Name");
        this.name = name;
    }

    public void setDescription(@NotNull String description) {
        Checks.notEmpty(description, "Description");
        Checks.notLonger(description, 100, "Description");
        this.description = description;
    }
}
