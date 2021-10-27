package ru.herobrine1st.fusion.api.command.option.parser;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.exception.NoSuchArgumentException;

public class ChannelParserElement extends ParserElement<ChannelParserElement, GuildChannel> {

    public ChannelParserElement(String name, String description) {
        super(name, description);
    }

    @Override
    public @NotNull OptionData getOptionData() {
        return new OptionData(OptionType.CHANNEL, name, description, required);
    }

    @Override
    public GuildChannel parseSlash(CommandContext ctx, @NotNull CommandInteraction interaction) throws NoSuchArgumentException {
        OptionMapping option = interaction.getOption(name);
        if(option == null) throw new NoSuchArgumentException(this);
        return option.getAsGuildChannel();
    }

}
