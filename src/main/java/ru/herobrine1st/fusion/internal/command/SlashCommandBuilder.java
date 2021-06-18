package ru.herobrine1st.fusion.internal.command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.Nullable;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.args.ParserElement;
import ru.herobrine1st.fusion.api.command.declare.FusionCommandData;
import ru.herobrine1st.fusion.api.command.declare.FusionSubcommandData;
import ru.herobrine1st.fusion.api.command.declare.FusionSubcommandGroupData;

import java.util.Collection;
import java.util.stream.Stream;

public class SlashCommandBuilder {
    private SlashCommandBuilder() {
    }

    private static Collection<SubcommandData> subcommandDataFromSubcommands(Stream<FusionSubcommandData> data) {
        return data.map(it -> new SubcommandData(it.getName(), it.getDescription())
                .addOptions(it.getOptions().stream()
                        .map(that -> (ParserElement) that)
                        .map(ParserElement::getOptionData).toList()
                )).toList();
    }

    @Nullable
    public static CommandData buildCommand(FusionCommandData fusionCommandData) {
        if (!hasSlashSupport(fusionCommandData)) return null;
        var commandData = new CommandData(fusionCommandData.getName(), fusionCommandData.getDescription());
        if (fusionCommandData.hasExecutor()) {
            return commandData.addOptions(fusionCommandData.getOptions().stream()
                    .map(it -> (ParserElement) it)
                    .map(ParserElement::getOptionData)
                    .toList());
        }
        if (fusionCommandData.hasSubcommands()) {
            return commandData.addSubcommands(subcommandDataFromSubcommands(fusionCommandData.getOptions()
                    .stream().map(it -> (FusionSubcommandData) it)));
        }
        if (fusionCommandData.hasSubcommandGroups()) {
            return commandData.addSubcommandGroups(
                    fusionCommandData.getOptions().stream()
                            .map(it -> (FusionSubcommandGroupData) it)
                            .map(it -> new SubcommandGroupData(it.getName(), it.getDescription())
                                    .addSubcommands(subcommandDataFromSubcommands(it.getSubcommandData().stream()))
                            ).toList()
            );
        }
        throw new IllegalArgumentException();
    }

    public static boolean hasSlashSupport(FusionCommandData commandData) {
        if(commandData.getPermissionHandler().allowedTypes().equals(PermissionHandler.Type.MESSAGE))
            return false;
        if (commandData.hasExecutor())
            return commandData.getOptions().stream()
                    .map(it -> (ParserElement) it)
                    .allMatch(ParserElement::hasSlashSupport);
        if (commandData.hasSubcommands())
            return commandData.getOptions().stream()
                    .map(it -> (FusionSubcommandData) it)
                    .flatMap(it -> it.getOptions().stream())
                    .map(it -> (ParserElement) it)
                    .allMatch(ParserElement::hasSlashSupport);
        if (commandData.hasSubcommandGroups())
            return commandData.getOptions().stream()
                    .map(it -> (FusionSubcommandGroupData) it)
                    .flatMap(it -> it.getSubcommandData().stream())
                    .flatMap(it -> it.getOptions().stream())
                    .map(it -> (ParserElement) it)
                    .allMatch(ParserElement::hasSlashSupport);
        throw new IllegalArgumentException();
    }
}
