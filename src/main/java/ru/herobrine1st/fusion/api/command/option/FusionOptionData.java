package ru.herobrine1st.fusion.api.command.option;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.option.parser.ParserElement;

public abstract /*sealed*/ class FusionOptionData /*permits FusionBaseCommand, FusionSubcommandGroup, ParserElement*/ {
    protected final String name, description;

    public FusionOptionData(@NotNull String name, @NotNull String description) {
        Checks.notEmpty(name, "Name");
        Checks.notLonger(name, 32, "Name");
        Checks.isLowercase(name, "Name");
        Checks.matches(name, Checks.ALPHANUMERIC_WITH_DASH, "Name");
        Checks.notEmpty(description, "Description");
        Checks.notLonger(description, 100, "Description");
        this.name = name;
        this.description = description;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

}
