package ru.herobrine1st.fusion.module.test;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import ru.herobrine1st.fusion.api.command.args.GenericArguments;
import ru.herobrine1st.fusion.api.command.build.FusionCommandData;
import ru.herobrine1st.fusion.api.manager.CommandManager;
import ru.herobrine1st.fusion.api.module.AbstractModule;
import ru.herobrine1st.fusion.api.module.FutureModule;

@FutureModule(id = "testmodule")
public class TestModule extends AbstractModule {

    @Override
    public void registerCommands(CommandManager commandManager) {
        commandManager.addCommand(new FusionCommandData("fusiontest", "description")
                .addArguments(GenericArguments.remainingJoinedStrings("string", "Пиши блять сюда текст"))
                .setExecutor(ctx -> ctx
                        .replyWaitingClick(
                                ctx.getEmbedBase()
                                        .setDescription("Успешный тест! " + ctx.<String>getOne("string")
                                                .orElse("Да хуй он там успешный блять"))
                                        .build(),
                                ActionRow.of(Button.primary("Test", "Test button 1"), Button.secondary("Test2", "Test button 2")),
                                ActionRow.of(Button.success("Test3", "Test button 3"), Button.danger("test4", "Test button 4")))
                        .flatMap(event -> ctx.reply(ctx.getEmbedBase()
                                .setDescription(event.getComponentId())
                                .build()))
                        .queue(null, ctx::replyException)
                ));

    }

    @Override
    public void registerListener(JDA jda) {
        // Do nothing
    }
}
