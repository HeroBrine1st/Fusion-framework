package ru.herobrine1st.fusion.module.test;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.build.FusionCommandData;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandData;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandGroupData;
import ru.herobrine1st.fusion.api.manager.CommandManager;
import ru.herobrine1st.fusion.api.module.AbstractModule;
import ru.herobrine1st.fusion.api.module.FutureModule;

@FutureModule(id = "testmodule")
public class TestModule extends AbstractModule {

    @Override
    public void registerCommands(CommandManager commandManager) {
        commandManager.addCommand(new FusionCommandData("fusiontest", "Тестовая команда")
                //.addArguments(GenericArguments.remainingJoinedStrings("string", "Пиши блять сюда текст"))
                //.setPermissionHandler(new PermissionHandler.Typed(PermissionHandler.CommandType.MESSAGE))
                .setExecutor(ctx -> ctx
                        .reply(
                                ctx.getEmbedBase()
                                        .setDescription("Успешный тест!")
                                        .build(),
                                ActionRow.of(Button.danger("reply_10_seconds", "Ответить через 10 секунд")))
                        .flatMap(it -> ctx.getButtonClickEventRestAction())
                        .flatMap(event -> ctx.reply(ctx.getEmbedBase()
                                .setDescription(event.getComponentId())
                                .build())
                        )
                        .queue(null, ctx::replyException)
                ));
        commandManager.addCommand(new FusionCommandData("test2", "Тестовая команда с группами")
                .setPermissionHandler(new PermissionHandler.Typed(PermissionHandler.CommandType.MESSAGE))
                .addSubcommandGroups(new FusionSubcommandGroupData("group", "Группа")
                        .addSubcommands(
                                new FusionSubcommandData("subcommand", "Субкоманда")
                                        .setExecutor(ctx -> ctx.reply(ctx.getEmbedBase().setDescription("123").build()).queue())
                        )
                )
        );
        commandManager.addCommand(new FusionCommandData("test3", "Тестовая команда с субкомандами")
                .setPermissionHandler(new PermissionHandler.Typed(PermissionHandler.CommandType.MESSAGE))
                .addSubcommands(
                        new FusionSubcommandData("subcommand", "Субкоманда")
                                .setExecutor(ctx -> ctx.reply(ctx.getEmbedBase().setDescription("123").build()).queue())
                )
        );

    }

    @Override
    public void registerListener(JDA jda) {
        // Do nothing
    }
}
