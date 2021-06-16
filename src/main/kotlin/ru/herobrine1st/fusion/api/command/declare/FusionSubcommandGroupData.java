package ru.herobrine1st.fusion.api.command.declare;

import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FusionSubcommandGroupData extends BaseCommand<FusionSubcommandData> {
    private final List<FusionSubcommandData> subcommandData = new ArrayList<>();

    public FusionSubcommandGroupData(@NotNull String name, @NotNull String description) {
        super(name, description);
    }


    public List<FusionSubcommandData> getSubcommandData() {
        return subcommandData;
    }

    public FusionSubcommandGroupData addSubcommands(FusionSubcommandData... data) {
        Checks.check(data.length + subcommandData.size() <= 25, "Cannot have more than 25 subcommands for a subcommand group!")
        subcommandData.addAll(List.of(data));
        return this;
    }
}
