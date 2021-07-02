package ru.herobrine1st.fusion.internal.command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.Nullable;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.args.ParserElement;
import ru.herobrine1st.fusion.api.command.build.FusionCommandData;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandData;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandGroupData;

import java.util.Collection;
import java.util.stream.Stream;

public final class SlashCommandBuilder {
    private SlashCommandBuilder() {
    }

    private static Collection<SubcommandData> subcommandDataFromSubcommands(Stream<FusionSubcommandData> data) {
        return data.map(it -> new SubcommandData(it.getName(), it.getDescription())
                .addOptions(it.getOptions().stream()
                        .map(ParserElement.class::cast)
                        .map(ParserElement::getOptionData).toList()
                )).toList();
    }

    @Nullable
    public static CommandData buildCommand(FusionCommandData fusionCommandData) {
        if (!hasSlashSupport(fusionCommandData)) return null;
        var commandData = new CommandData(fusionCommandData.getName(), fusionCommandData.getDescription());
        if (fusionCommandData.hasExecutor()) {
            return commandData.addOptions(fusionCommandData.getOptions().stream()
                    .map(ParserElement.class::cast)
                    .map(ParserElement::getOptionData)
                    .toList());
        }
        if (fusionCommandData.hasSubcommands()) {
            return commandData.addSubcommands(subcommandDataFromSubcommands(fusionCommandData.getOptions()
                    .stream().map(FusionSubcommandData.class::cast)));
        }
        if (fusionCommandData.hasSubcommandGroups()) {
            return commandData.addSubcommandGroups(
                    fusionCommandData.getOptions().stream()
                            .map(FusionSubcommandGroupData.class::cast)
                            .map(it -> new SubcommandGroupData(it.getName(), it.getDescription())
                                    .addSubcommands(subcommandDataFromSubcommands(it.getSubcommandData().stream()))
                            ).toList()
            );
        }
        throw new IllegalArgumentException();
    }

    public static boolean hasSlashSupport(FusionCommandData commandData) {
        if(!commandData.getPermissionHandler().commandType().slashExecutionPermitted())
            return false;
        if (commandData.hasExecutor())
            return commandData.getOptions().stream()
                    .map(ParserElement.class::cast)
                    .allMatch(ParserElement::hasSlashSupport);
        if (commandData.hasSubcommands())
            return commandData.getOptions().stream()
                    .map(FusionSubcommandData.class::cast)
                    .flatMap(it -> it.getOptions().stream())
                    .map(ParserElement.class::cast)
                    .allMatch(ParserElement::hasSlashSupport);
        if (commandData.hasSubcommandGroups())
            return commandData.getOptions().stream()
                    .map(FusionSubcommandGroupData.class::cast)
                    .flatMap(it -> it.getSubcommandData().stream())
                    .flatMap(it -> it.getOptions().stream())
                    .map(ParserElement.class::cast)
                    .allMatch(ParserElement::hasSlashSupport);
        throw new IllegalArgumentException();
    }
}
