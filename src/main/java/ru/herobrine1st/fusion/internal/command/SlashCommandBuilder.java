package ru.herobrine1st.fusion.internal.command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import ru.herobrine1st.fusion.api.command.option.*;
import ru.herobrine1st.fusion.api.command.option.parser.ParserElement;

import java.util.Collection;

public final class SlashCommandBuilder {

    private SlashCommandBuilder() {
    }

    private static Collection<SubcommandData> subcommandDataFromSubcommands(Collection<FusionSubcommand> data) {
        return data.stream().map(it -> new SubcommandData(it.getName(), it.getDescription())
                .addOptions(it.getOptions().stream()
                        .map(ParserElement::getOptionData).toList()
                )).toList();
    }

    public static CommandData buildCommand(FusionCommand<?> fusionCommand) {
        var commandData = new CommandData(fusionCommand.getName(), fusionCommand.getDescription());
        if (fusionCommand instanceof FusionCommandWithArguments fusionCommandWithArguments) {
            return commandData.addOptions(fusionCommandWithArguments.getOptions().stream()
                    .map(ParserElement::getOptionData)
                    .toList());
        }
        if (fusionCommand instanceof FusionCommandWithSubcommands fusionCommandWithSubcommands) {
            return commandData.addSubcommands(subcommandDataFromSubcommands(fusionCommandWithSubcommands.getOptions()));
        }
        if (fusionCommand instanceof FusionCommandWithSubcommandGroups fusionCommandWithSubcommandGroups) {
            return commandData.addSubcommandGroups(
                    fusionCommandWithSubcommandGroups.getOptions().stream()
                            .map(it -> new SubcommandGroupData(it.getName(), it.getDescription())
                                    .addSubcommands(subcommandDataFromSubcommands(it.getSubcommandData()))
                            ).toList()
            );
        }
        throw new IllegalArgumentException();
    }
}
