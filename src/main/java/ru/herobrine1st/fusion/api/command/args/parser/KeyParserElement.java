package ru.herobrine1st.fusion.api.command.args.parser;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.args.CommandArgs;
import ru.herobrine1st.fusion.api.command.args.ParserElement;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;

import java.util.Optional;

/**
 * Wrapper around any ParserElement. Allows using option as {@code --name=value}: {@code name} is name of element, {@code value} is user input.
 * @param <R> Parse result type of wrapped element
 */
public class KeyParserElement<R> extends ParserElement<KeyParserElement<R>, R> {

    private final ParserElement<?, R> element;
    private final R defaultValue;

    public KeyParserElement(ParserElement<?, R> element, R defaultValue) {
        super(element.getName(), element.getDescription());
        this.element = element;
        this.defaultValue = defaultValue;
        setRequired(false);

    }

    @Override
    public R parseValue(CommandArgs args, CommandContext ctx) throws ArgumentParseException {
        Optional<CommandArgs> value = args.getKey(getName());
        if (value.isEmpty())
            if (required) throw new ArgumentParseException(String.format("Ключ %s отсутствует", getName()));
            else return defaultValue;
        return element.parseValue(value.get(), ctx);
    }

    @Override
    public boolean hasSlashSupport() {
        return element.hasSlashSupport();
    }

    @Override
    public OptionData getOptionData() {
        return element.getOptionData();
    }

    @Override
    public void parseSlash(CommandContext ctx) throws ArgumentParseException {
        element.parseSlash(ctx);
    }

    @Override
    public R parseSlash(CommandContext ctx, CommandInteraction interaction) {
        return null;
    }

    @Override
    public String getUsage() {
        return element.getUsage();
    }

    @Override
    public String getRawUsage() {
        return element.getRawUsage();
    }

    @NotNull
    @Override
    public KeyParserElement<R> setRequired(boolean required) {
        element.setRequired(required);
        return this;
    }

    @Override
    public boolean isRequired() {
        return element.isRequired();
    }
}
