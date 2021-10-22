package ru.herobrine1st.fusion.api.command.args.parser;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandContext;

import java.util.Map;
import java.util.NoSuchElementException;

public class StringParserElement extends ChoicesParserElement<StringParserElement, String> {

    public StringParserElement(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean hasSlashSupport() {
        return true;
    }

    @Override
    public OptionData getOptionData() {
        OptionData optionData = new OptionData(OptionType.STRING, getName(), getDescription(), required);
        for(Map.Entry<String, String> entry: choices.entrySet()) {
            optionData.addChoice(entry.getKey(), entry.getValue());
        }
        return optionData;
    }

    @Override
    public String parseSlash(CommandContext ctx, @NotNull CommandInteraction interaction) {
        OptionMapping optionMapping = interaction.getOption(getName());
        if (optionMapping == null) throw new NoSuchElementException();
        return optionMapping.getAsString();
    }

    @NotNull
    @Override
    public StringParserElement addChoice(String name, String value) {
        Checks.notLonger(value, 100, "Value");
        return super.addChoice(name, value);
    }
}
