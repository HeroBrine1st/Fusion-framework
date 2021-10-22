package ru.herobrine1st.fusion.api.command.args.parser;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;

import java.util.Map;
import java.util.NoSuchElementException;

public class IntegerParserElement extends ChoicesParserElement<IntegerParserElement, Long> {

    private final long min;
    private final long max;

    public IntegerParserElement(String name, String description, long min, long max) {
        super(name, description);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean hasSlashSupport() {
        return true;
    }

    @Override
    public OptionData getOptionData() {
        OptionData optionData = new OptionData(OptionType.INTEGER, name, description, required);
        for (Map.Entry<String, Long> entry : choices.entrySet()) {
            optionData.addChoice(entry.getKey(), entry.getValue().intValue()); // FIXME range -2^31..2^31-1; Waiting library fix
        }
        return optionData;
    }

    @Override
    public Long parseSlash(CommandContext ctx, @NotNull CommandInteraction interaction) throws ArgumentParseException {
        OptionMapping option = interaction.getOption(name);
        if (option == null) throw new NoSuchElementException();
        long res = option.getAsLong();
        if (res < min || res > max)
            throw new ArgumentParseException("Integer %S is out of range %s..%s".formatted(name, min, max));
        return res;
    }


}
