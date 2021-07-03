package ru.herobrine1st.fusion.internal.manager;

import net.dv8tion.jda.internal.utils.Checks;
import ru.herobrine1st.fusion.api.command.FusionOptionData;
import ru.herobrine1st.fusion.api.command.args.ParserElement;
import ru.herobrine1st.fusion.api.command.build.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.build.FusionCommandData;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandData;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandGroupData;
import ru.herobrine1st.fusion.api.manager.CommandManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManagerImpl implements CommandManager {
    public static final CommandManagerImpl INSTANCE = new CommandManagerImpl();
    public final List<FusionCommandData> commands = new ArrayList<>();

    public static String usage(FusionBaseCommand<?> command) {
        if (command.hasSubcommandGroups() || command.hasSubcommands())
            return command.getOptions().stream()
                    .map(FusionOptionData::getName)
                    .collect(Collectors.joining("|", "<", ">"));
        else
            return command.getArguments().stream()
                    .map(ParserElement::getUsage)
                    .collect(Collectors.joining(" "));
    }

    public static String usage(FusionSubcommandGroupData subcommandGroupData) {
        return subcommandGroupData.getSubcommandData().stream()
                .map(FusionOptionData::getName)
                .collect(Collectors.joining("|", "<", ">"));
    }

    private CommandManagerImpl() {
    }

    @Override
    public void addCommand(FusionCommandData data) {
        if (commands.stream().map(FusionBaseCommand::getName).anyMatch(it -> it.equals(data.getName()))) {
            throw new RuntimeException("Intersecting name: " + data.getName());
        }
        // Валидация
        if (data.hasSubcommandGroups())
            Checks.check(data.getOptions().stream().map(it -> (FusionSubcommandGroupData) it)
                            .flatMap(it -> it.getSubcommandData().stream())
                            .allMatch(FusionBaseCommand::hasExecutor),
                    "All subcommands must have an executor");
        else if (data.hasSubcommands())
            Checks.check(data.getOptions().stream().map(it -> (FusionSubcommandData) it)
                            .allMatch(FusionBaseCommand::hasExecutor),
                    "All subcommands must have an executor");
        else Checks.check(data.hasExecutor(), "Command must have an executor");
        commands.add(data);
    }
}