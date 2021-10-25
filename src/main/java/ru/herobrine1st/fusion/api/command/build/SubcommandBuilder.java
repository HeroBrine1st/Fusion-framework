package ru.herobrine1st.fusion.api.command.build;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandExecutor;
import ru.herobrine1st.fusion.api.command.FusionSubcommand;
import ru.herobrine1st.fusion.api.command.args.parser.ParserElement;

public final class SubcommandBuilder extends BaseCommandBuilder<SubcommandBuilder, ParserElement<?, ?>> {
    private CommandExecutor executor = null;

    public SubcommandBuilder(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    public SubcommandBuilder setExecutor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public FusionSubcommand build() {
        Checks.check(this.options.size() <= 25, "Cannot have more than 25 options for a command!");
        Checks.check(
                this.options.stream()
                        .dropWhile(it -> it.getOptionData().isRequired())
                        .noneMatch(it -> it.getOptionData().isRequired()),
                "You should add non-required arguments after required ones");
        Checks.notNull(executor, "Executor");
        return new FusionSubcommand(name, description, executor, options, shortName, permissionHandler);
    }
}