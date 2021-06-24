package ru.herobrine1st.fusion.api.command.args;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.internal.command.args.CommandArgs;

import java.util.NoSuchElementException;

public abstract class ParserElement {
    private final String key;
    private final String description;

    protected ParserElement(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }
    public String getDescription() {
        return description;
    }

    public void parse(CommandArgs args, CommandContext ctx) throws ArgumentParseException {
        Object value;
        try {
            value = parseValue(args, ctx);
        } catch (NoSuchElementException e) {
            throw ArgumentParseException.withPointer("Обработчик вышел за пределы массива", args);
        }
        if (key != null && value != null) {
            ctx.putArg(key, value);
        }
    }

    protected abstract Object parseValue(CommandArgs args, CommandContext ctx) throws ArgumentParseException;

    public abstract boolean hasSlashSupport();
    public abstract OptionData getOptionData();

    public void parseSlash(CommandContext ctx) throws ArgumentParseException {
        Object value;
        try {
            value = parseSlash(ctx, (CommandInteraction) ctx.getEvent());
        } catch(NoSuchElementException e) {
            throw new ArgumentParseException("Обработчик не обнаружил ключа " + e.getMessage());
        }
        if(key != null && value != null) {
            ctx.putArg(key, value);
        }
    }

    public abstract Object parseSlash(CommandContext ctx, CommandInteraction interaction) throws ArgumentParseException;

    public String getUsage() {
        return "<" + getRawUsage() + ">";
    }

    public abstract String getRawUsage();
}
