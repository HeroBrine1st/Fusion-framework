package ru.herobrine1st.fusion.api.command.option.parser;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.exception.NoSuchArgumentException;

public class RoleParserElement extends ParserElement<RoleParserElement, Role> {

    public RoleParserElement(String name, String description) {
        super(name, description);
    }

    @Override
    public @NotNull OptionData getOptionData() {
        return new OptionData(OptionType.ROLE, name, description, required);
    }

    @Override
    public Role parseSlash(CommandContext ctx, CommandInteraction interaction) throws NoSuchArgumentException {
        OptionMapping option = interaction.getOption(name);
        if(option == null) throw new NoSuchArgumentException(this);
        return option.getAsRole();
    }

}
