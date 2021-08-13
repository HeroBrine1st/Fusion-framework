package ru.herobrine1st.fusion.api.command.args.parser;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.args.CommandArgs;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;

import java.util.Locale;
import java.util.NoSuchElementException;

public class BooleanParserElement extends ParserElement<BooleanParserElement, Boolean> {

    public BooleanParserElement(String name, String description) {
        super(name, description);
    }

    @Override
    public Boolean parseValue(CommandArgs args, CommandContext ctx) throws ArgumentParseException {
        String value = args.next().getValue();
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "true", "yes", "y" -> true;
            case "false", "no", "n" -> false;
            default -> throw ArgumentParseException.withPointer("Неправильный формат boolean", args);
        };
    }

    @Override
    public boolean hasSlashSupport() {
        return true;
    }

    @Override
    public OptionData getOptionData() {
        return new OptionData(OptionType.BOOLEAN, name, description, required);
    }

    @Override
    public Boolean parseSlash(CommandContext ctx, CommandInteraction interaction) {
        OptionMapping option = interaction.getOption(name);
        if(option == null) throw new NoSuchElementException();
        return option.getAsBoolean();
    }

    @Override
    public String getRawUsage() {
        return "boolean";
    }
}
