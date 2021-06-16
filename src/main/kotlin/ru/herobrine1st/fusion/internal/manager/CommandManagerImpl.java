package ru.herobrine1st.fusion.internal.manager;

import ru.herobrine1st.fusion.api.command.args.ParserElement;
import ru.herobrine1st.fusion.api.command.declare.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.declare.FusionCommandData;
import ru.herobrine1st.fusion.api.command.declare.FusionSubcommandData;
import ru.herobrine1st.fusion.api.command.declare.FusionSubcommandGroupData;
import ru.herobrine1st.fusion.api.manager.CommandManager;

import java.util.ArrayList;
import java.util.List;

public class CommandManagerImpl implements CommandManager {
    public static final CommandManagerImpl INSTANCE = new CommandManagerImpl();
    public final List<FusionCommandData> commands = new ArrayList<>();

    private CommandManagerImpl() {
    }

    @Override
    public void addCommand(FusionCommandData data) {
        if (commands.stream().map(FusionBaseCommand::getName).anyMatch(it -> it.equals(data.getName()))) {
            throw new RuntimeException("Intersecting name: " + data.getName());
        }
        commands.add(data);
    }

    public static boolean checkSlashSupport(FusionCommandData command) { // Да-да компилятор должен выкинуть unchecked cast
        if (command.hasExecutor())
            return command.getOptions().stream()
                    .map(it -> (ParserElement) it)
                    .allMatch(ParserElement::hasSlashSupport);
        if (command.hasSubcommands())
            return command.getOptions().stream()
                    .map(it -> (FusionSubcommandData) it)
                    .flatMap(it -> it.getOptions().stream())
                    .map(it -> (ParserElement) it)
                    .allMatch(ParserElement::hasSlashSupport);
        if (command.hasSubcommandGroups())
            return command.getOptions().stream()
                    .map(it -> (FusionSubcommandGroupData) it)
                    .flatMap(it -> it.getSubcommandData().stream())
                    .flatMap(it -> it.getOptions().stream())
                    .map(it -> (ParserElement) it)
                    .allMatch(ParserElement::hasSlashSupport);
        throw new IllegalArgumentException();
    }
}