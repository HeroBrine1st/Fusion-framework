package ru.herobrine1st.fusion.module.base;

import net.dv8tion.jda.api.JDA;
import ru.herobrine1st.fusion.api.command.args.GenericArguments;
import ru.herobrine1st.fusion.api.command.build.FusionCommandData;
import ru.herobrine1st.fusion.api.manager.CommandManager;
import ru.herobrine1st.fusion.api.module.AbstractModule;
import ru.herobrine1st.fusion.api.module.FutureModule;

@FutureModule(id = "base")
public class BaseModule extends AbstractModule {
    @Override
    public void registerCommands(CommandManager commandManager) {
        commandManager.addCommand(new FusionCommandData("help", "Помощь по классическим командам")
                .addArguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings("command", "Команда, для которой необходима страница помощи")))

        );
    }

    @Override
    public void registerListener(JDA jda) {
        // Do nothing
    }
}
