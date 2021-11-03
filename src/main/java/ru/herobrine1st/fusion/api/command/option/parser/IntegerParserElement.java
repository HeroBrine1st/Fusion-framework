package ru.herobrine1st.fusion.api.command.option.parser;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;
import ru.herobrine1st.fusion.api.exception.NoSuchArgumentException;

import java.util.Map;

public class IntegerParserElement extends ChoicesParserElement<IntegerParserElement, Long> {

    private final long min;
    private final long max;

    public IntegerParserElement(String name, String description, long min, long max) {
        super(name, description);
        this.min = min;
        this.max = max;
    }

    @Override
    public @NotNull OptionData getOptionData() {
        OptionData optionData = new OptionData(OptionType.INTEGER, name, description, required);
        for (Map.Entry<String, Long> entry : choices.entrySet()) {
            optionData.addChoice(entry.getKey(), entry.getValue());
        }
        return optionData;
    }

    @Override
    public Long parseSlash(CommandContext ctx, @NotNull CommandInteraction interaction) throws ArgumentParseException {
        OptionMapping option = interaction.getOption(name);
        if (option == null) throw new NoSuchArgumentException(this);
        long res = option.getAsLong();
        if (res < min || res > max)
            throw new ArgumentParseException("Integer %s is out of range %s..%s".formatted(name, min, max));
        return res;
    }


}
