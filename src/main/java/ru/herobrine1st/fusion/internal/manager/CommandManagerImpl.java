package ru.herobrine1st.fusion.internal.manager;

import ru.herobrine1st.fusion.api.command.declare.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.declare.FusionCommandData;
import ru.herobrine1st.fusion.api.manager.CommandManager;

import java.util.ArrayList;
import java.util.List;

public class CommandManagerImpl implements CommandManager {
    public static final CommandManagerImpl INSTANCE = new CommandManagerImpl();
    public final List<FusionCommandData> commands = new ArrayList<>();

    private CommandManagerImpl() {}

    @Override
    public void addCommand(FusionCommandData data) {
        if (commands.stream().map(FusionBaseCommand::getName).anyMatch(it -> it.equals(data.getName()))) {
            throw new RuntimeException("Intersecting name: " + data.getName());
        }
        commands.add(data);
    }
}