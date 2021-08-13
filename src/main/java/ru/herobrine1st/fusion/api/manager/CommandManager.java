package ru.herobrine1st.fusion.api.manager;

import ru.herobrine1st.fusion.api.command.build.FusionCommand;

public interface CommandManager {
    void registerCommand(FusionCommand<?> data);
}
