package ru.herobrine1st.fusion.api.manager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import ru.herobrine1st.fusion.api.command.FusionOptionData;
import ru.herobrine1st.fusion.api.command.args.parser.ParserElement;
import ru.herobrine1st.fusion.api.command.build.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.build.FusionCommand;
import ru.herobrine1st.fusion.api.command.build.FusionSubcommandGroup;
import ru.herobrine1st.fusion.internal.manager.CommandManagerImpl;

import java.util.List;
import java.util.stream.Collectors;

public abstract class CommandManager {
    public static String usage(FusionBaseCommand<?, ?> command) {
        if (command instanceof FusionCommand.WithArguments cmd) {
            return cmd.getOptions().stream()
                    .map(ParserElement::getUsage)
                    .collect(Collectors.joining(" "));
        }
        return command.getOptions().stream()
                .map(FusionOptionData::getName)
                .collect(Collectors.joining("|", "<", ">"));
    }

    public static String usage(FusionSubcommandGroup subcommandGroupData) {
        return subcommandGroupData.getSubcommandData().stream()
                .map(FusionOptionData::getName)
                .collect(Collectors.joining("|", "<", ">"));
    }

    public static CommandManager create(JDA jda, String commandPrefix) {
        return new CommandManagerImpl(jda, commandPrefix);
    }

    public static CommandManager create(JDA jda) {
        return create(jda, "/");
    }

    public abstract void registerCommand(FusionCommand<?> data);

    public abstract List<FusionCommand<?>> getCommands();

    public abstract String getCommandPrefix();

    public abstract void setCommandPrefix(String prefix);

    public abstract void sendSlashCommands(Guild testingGuild);

    public void sendSlashCommands() {
        sendSlashCommands(null);
    }
}
