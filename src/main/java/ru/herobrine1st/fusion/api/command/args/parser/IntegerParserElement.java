package ru.herobrine1st.fusion.api.command.args.parser;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.args.CommandArgs;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;

import java.util.NoSuchElementException;

public class IntegerParserElement extends ParserElement<IntegerParserElement, Long> {

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
        long res;
        try {
            res = Long.parseLong(args.next().getValue(), radix);
        } catch(NumberFormatException e) {
            throw ArgumentParseException.withPointer("Неправильный формат числа", args);
        }
        if(res < min || res > max) throw ArgumentParseException.withPointer("Число выходит за предел %s..%s".formatted(min, max), args);
        return res;
    }

    @Override
    public boolean hasSlashSupport() {
        return true;
    }

    @Override
    public OptionData getOptionData() {
        return new OptionData(OptionType.INTEGER, name, description, required);
    }

    @Override
    public Long parseSlash(CommandContext ctx, @NotNull CommandInteraction interaction) throws ArgumentParseException {
        OptionMapping option = interaction.getOption(name);
        if(option == null) throw new NoSuchElementException();
        long res = option.getAsLong();
        if(res < min || res > max) throw new ArgumentParseException("Аргумент %S выходит за предел %s..%s".formatted(name, min, max));
        return res;
    }

    @Override
    public String getRawUsage() {
        return "integer";
    }
}
