package ru.herobrine1st.fusion.module.base;

import net.dv8tion.jda.api.hooks.SubscribeEvent;
import ru.herobrine1st.fusion.api.command.args.GenericArguments;
import ru.herobrine1st.fusion.api.command.build.FusionCommandData;
import ru.herobrine1st.fusion.api.event.FusionInitializationEvent;
import ru.herobrine1st.fusion.api.manager.CommandManager;
import ru.herobrine1st.fusion.api.module.FutureModule;
import ru.herobrine1st.fusion.module.base.command.HelpCommand;

import javax.inject.Inject;

@FutureModule(id = "base")
public class BaseModule {
    @Inject
    CommandManager commandManager;

    @SubscribeEvent
    public void onInit(FusionInitializationEvent event) {
        commandManager.registerCommand(new FusionCommandData("help", "Помощь по классическим командам")
                .addArguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings("command", "Команда, для которой необходима страница помощи")))
                .setExecutor(new HelpCommand())
                .setShortName("Помощь")
        );
    }
}
