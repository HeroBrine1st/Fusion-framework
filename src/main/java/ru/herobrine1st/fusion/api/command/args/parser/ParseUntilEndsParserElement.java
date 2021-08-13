package ru.herobrine1st.fusion.api.command.args.parser;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.args.CommandArgs;
import ru.herobrine1st.fusion.api.command.args.ParserElement;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;

public class ParseUntilEndsParserElement<T> extends ParserElement<ParseUntilEndsParserElement<T>, T> {

    private final ParserElement<?, T> element;
    private final int minCount;

    public ParseUntilEndsParserElement(ParserElement<?, T> element, int minCount) {
        super(element.getName(), element.getDescription());
        this.element = element.setRequired(true);
        this.minCount = minCount;
    }

    @Override
    public @NotNull String getName() {
        return element.getName();
    }

    @Override
    public @NotNull String getDescription() {
        return element.getDescription();
    }

    @Override
    public void parse(CommandArgs args, CommandContext ctx) throws ArgumentParseException {
        int i = 0;
        for (; args.hasNext(); i++) {
            element.parse(args, ctx);
        }
        if (i < minCount) throw ArgumentParseException.withPointer(
                String.format("Аргумент %s должен повторяться хотя бы %s раз%s",
                        element.getRawUsage(),
                        minCount,
                        (minCount >= 2 && minCount <= 5) || ((minCount > 20) && (minCount % 10 >= 2) && (minCount % 10 <= 5)) ? "а" : ""), args);
    }

    @Override
    public T parseValue(CommandArgs args, CommandContext ctx) {
        return null;
    }

    @Override
    public boolean hasSlashSupport() {
        return false; // There's no vararg support in Discord :C
    }

    @Override
    public OptionData getOptionData() {
        return null;
    }

    @Override
    public T parseSlash(CommandContext ctx, CommandInteraction interaction) {
        return null;
    }

    @Override
    public String getRawUsage() {
        return element.getRawUsage() + "...";
    }

    @Override
    public String getUsage() {
        return element.getUsage() + "...";
    }

    @NotNull
    @Override
    public ParseUntilEndsParserElement<T> setRequired(boolean required) {
        throw new UnsupportedOperationException();
    }
}