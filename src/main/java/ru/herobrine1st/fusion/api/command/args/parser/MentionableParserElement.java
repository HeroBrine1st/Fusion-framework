package ru.herobrine1st.fusion.api.command.args.parser;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandContext;

import java.util.NoSuchElementException;

public class MentionableParserElement extends ParserElement<MentionableParserElement, IMentionable> {
    public MentionableParserElement(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean hasSlashSupport() {
        return true;
    }

    @Override
    public OptionData getOptionData() {
        return new OptionData(OptionType.MENTIONABLE, name, description, required);
    }

    @Override
    public IMentionable parseSlash(CommandContext ctx, @NotNull CommandInteraction interaction) {
        OptionMapping option = interaction.getOption(name);
        if(option == null) throw new NoSuchElementException();
        return option.getAsMentionable();
    }

}
