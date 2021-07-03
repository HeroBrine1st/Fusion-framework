package ru.herobrine1st.fusion.module.test;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import ru.herobrine1st.fusion.api.command.build.FusionCommandData;
import ru.herobrine1st.fusion.api.manager.CommandManager;
import ru.herobrine1st.fusion.api.module.AbstractModule;
import ru.herobrine1st.fusion.api.module.FutureModule;

import java.util.concurrent.TimeUnit;

@FutureModule(id = "testmodule")
public class TestModule extends AbstractModule {

    @Override
    public void registerCommands(CommandManager commandManager) {
        commandManager.addCommand(new FusionCommandData("fusiontest", "description")
                // .addArguments(GenericArguments.remainingJoinedStrings("string", "Пиши блять сюда текст"))
                .setExecutor(ctx -> ctx
                        .replyWaitingClick(
                                ctx.getEmbedBase()
                                        .setDescription("Успешный тест!")
                                        .build(),
                                ActionRow.of(Button.danger("reply_10_seconds", "Ответить через 10 секунд")))
                        .delay(10, TimeUnit.SECONDS)
                        .flatMap(event ->
                                ctx.reply(ctx.getEmbedBase()
                                .setDescription(event.getComponentId())
                                        .build())
                        )
                        .queue(null, ctx::replyException)
                ));


    }

    @Override
    public void registerListener(JDA jda) {
        // Do nothing
    }
}
