package ru.herobrine1st.fusion.module.test;

import net.dv8tion.jda.api.JDA;
import ru.herobrine1st.fusion.api.command.CommandResult;
import ru.herobrine1st.fusion.api.command.declare.FusionCommandData;
import ru.herobrine1st.fusion.api.manager.CommandManager;
import ru.herobrine1st.fusion.api.module.AbstractModule;
import ru.herobrine1st.fusion.api.module.FutureModule;

@FutureModule(id="testmodule")
public class TestModule extends AbstractModule {
    @Override
    public void registerCommands(CommandManager commandManager) {
        var command = new FusionCommandData("fusiontest", "description")
                .setExecutor(ctx -> ctx.reply(
                        new CommandResult.Builder()
                                .embed(ctx.getEmbedBase()
                                        .setDescription("Успешный тест!")
                                        .setColor(ctx.getColor())
                                        .setFooter(ctx.getFooter())
                                        .build())
                                .build()
                        )
                );
        commandManager.addCommand(command);

    }

    @Override
    public void registerListener(JDA jda) {
        // Do nothing
    }
}
