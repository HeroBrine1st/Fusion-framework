package ru.herobrine1st.fusion.internal.command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import ru.herobrine1st.fusion.api.command.args.parser.ParserElement;
import ru.herobrine1st.fusion.api.command.build.FusionCommand;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommand;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandGroup;

import java.util.Collection;
import java.util.stream.Stream;

public final class SlashCommandBuilder {

    private SlashCommandBuilder() {
    }

    private static Collection<SubcommandData> subcommandDataFromSubcommands(Stream<FusionSubcommand> data) {
        return data.map(it -> new SubcommandData(it.getName(), it.getDescription())
                .addOptions(it.getOptions().stream()
                        .map(ParserElement.class::cast)
                        .map(ParserElement::getOptionData).toList()
                )).toList();
    }

    public static CommandData buildCommand(FusionCommand<?> fusionCommand) {
        var commandData = new CommandData(fusionCommand.getName(), fusionCommand.getDescription());
        if (fusionCommand.hasExecutor()) {
            return commandData.addOptions(fusionCommand.getOptions().stream()
                    .map(ParserElement.class::cast)
                    .map(ParserElement::getOptionData)
                    .toList());
        }
        if (fusionCommand.hasSubcommands()) {
            return commandData.addSubcommands(subcommandDataFromSubcommands(fusionCommand.getOptions()
                    .stream().map(FusionSubcommand.class::cast)));
        }
        if (fusionCommand.hasSubcommandGroups()) {
            return commandData.addSubcommandGroups(
                    fusionCommand.getOptions().stream()
                            .map(FusionSubcommandGroup.class::cast)
                            .map(it -> new SubcommandGroupData(it.getName(), it.getDescription())
                                    .addSubcommands(subcommandDataFromSubcommands(it.getSubcommandData().stream()))
                            ).toList()
            );
        }
        throw new IllegalArgumentException();
    }
}
