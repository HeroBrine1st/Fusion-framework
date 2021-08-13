package ru.herobrine1st.fusion.api.command.args.parser;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.args.CommandArgs;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class IntegerParserElement extends ChoicesParserElement<IntegerParserElement, Long> {

    private final long min;
    private final long max;
    private final int radix;

    public IntegerParserElement(String name, String description, long min, long max, int radix) {
        super(name, description);
        this.min = min;
        this.max = max;
        this.radix = radix;
    }

    @Override
    public Long parseValue(@NotNull CommandArgs args, CommandContext ctx) throws ArgumentParseException {
        if(choices.isEmpty()) {
            long res;
            try {
                res = Long.parseLong(args.next().getValue(), radix);
            } catch (NumberFormatException e) {
                throw ArgumentParseException.withPointer("Illegal format number", args);
            }
            if (res < min || res > max)
                throw ArgumentParseException.withPointer("Integer is out of range %s..%s".formatted(min, max), args);
            return res;
        } else {
            Long value = choices.get(args.next().getValue());
            if (value == null) {
                throw ArgumentParseException.withPointer("Argument doesn't fit the choices: " +
                        choices.keySet().stream().map(it -> "\"" + it + "\"").collect(Collectors.joining(", ")), args);
            }
            return value;
        }
    }

    @Override
    public boolean hasSlashSupport() {
        return true;
    }

    @Override
    public OptionData getOptionData() {
        OptionData optionData = new OptionData(OptionType.INTEGER, name, description, required);
        for(Map.Entry<String, Long> entry: choices.entrySet()) {
            optionData.addChoice(entry.getKey(), entry.getValue().intValue()); // FIXME range -2^32..2^32-1; Waiting library fix
        }
        return optionData;
    }

    @Override
    public Long parseSlash(CommandContext ctx, @NotNull CommandInteraction interaction) throws ArgumentParseException {
        OptionMapping option = interaction.getOption(name);
        if(option == null) throw new NoSuchElementException();
        long res = option.getAsLong();
        if(res < min || res > max) throw new ArgumentParseException("Integer %S is out of range %s..%s".formatted(name, min, max));
        return res;
    }

    @Override
    public String getRawUsage() {
        if(choices.isEmpty())
            return "integer";
        else return String.join("|", choices.keySet());
    }
}
