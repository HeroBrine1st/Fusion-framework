package ru.herobrine1st.fusion.api.command.build;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandExecutor;
import ru.herobrine1st.fusion.api.command.args.ParserElement;

import java.util.List;

public class FusionCommandData extends FusionBaseCommand<FusionCommandData> {
    public FusionCommandData(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    private boolean allowArguments = true;
    private boolean allowSubcommands = true;
    private boolean allowSubcommandGroups = true;

    @NotNull
    @Override
    public FusionCommandData addArguments(ParserElement... elements) {
        Checks.check(allowArguments, "You cannot mix arguments with subcommands/groups.");
        allowSubcommandGroups = allowSubcommands = false;
        return super.addArguments(elements);
    }

    @NotNull
    @Override
    public FusionCommandData setExecutor(CommandExecutor executor) {
        Checks.check(allowArguments, "You cannot mix executor with subcommands/groups.");
        allowSubcommandGroups = allowSubcommands = false;
        return super.setExecutor(executor);
    }

    @NotNull
    public FusionCommandData addSubcommands(FusionSubcommandData... data) {
        Checks.check(allowSubcommands, "You cannot mix subcommands with arguments/groups.");
        Checks.noneNull(data, "Subcommand");
        Checks.check(data.length + this.options.size() <= 25, "Cannot have more than 25 options for a command!");
        allowArguments = allowSubcommandGroups = false;
        options.addAll(List.of(data));
        return this;
    }

    @NotNull
    public FusionCommandData addSubcommandGroups(FusionSubcommandGroupData... data) {
        Checks.check(allowSubcommandGroups, "You cannot mix subcommand groups with arguments/subcommsnds.");
        Checks.noneNull(data, "Subcommand group");
        Checks.check(data.length + this.options.size() <= 25, "Cannot have more than 25 options for a command!");
        allowArguments = allowSubcommands = false;
        options.addAll(List.of(data));
        return this;
    }
}