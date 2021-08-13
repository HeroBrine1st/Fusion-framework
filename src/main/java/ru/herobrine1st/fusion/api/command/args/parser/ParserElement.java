package ru.herobrine1st.fusion.api.command.args.parser;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.FusionOptionData;
import ru.herobrine1st.fusion.api.command.args.CommandArgs;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

/**
 * An generic argument parser.
 * @param <T> Type of class extending this class.
 * @param <R> Parse result type.
 */
public abstract class ParserElement<T extends ParserElement<T, R>, R> extends FusionOptionData {
    protected boolean required = true;

    public ParserElement(String name, String description) {
        super(name, description);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T setRequired(boolean required) {
        this.required = required;
        return (T) this;
    }

    public boolean isRequired() {
        return required;
    }

    public void parse(CommandArgs args, CommandContext ctx) throws ArgumentParseException {
        int pos = args.getPos();
        Object value;
        try {
            value = parseValue(args, ctx);
        } catch (NoSuchElementException e) {
            if (required) throw ArgumentParseException.withPointer("Обработчик вышел за пределы массива", args);
            else {args.setPos(pos);return;}
        }
        if (name != null && value != null) ctx.putArg(name, value);
    }

    public abstract R parseValue(CommandArgs args, CommandContext ctx) throws ArgumentParseException;

    public abstract boolean hasSlashSupport();
    public abstract OptionData getOptionData();

    public void parseSlash(CommandContext ctx) throws ArgumentParseException {
        R value;
        try {
            value = parseSlash(ctx, (CommandInteraction) ctx.getEvent());
        } catch(NoSuchElementException e) {
            if (required) throw new ArgumentParseException("Обработчик не обнаружил аргумента " + e.getMessage());
            else return;
        }
        if(name != null && value != null) {
            ctx.putArg(name, value);
        }
    }

    public abstract R parseSlash(CommandContext ctx, CommandInteraction interaction) throws ArgumentParseException;

    public String getUsage() {
        if(required) return "<" + getRawUsage() + ">";
        else return "[" + getRawUsage() + "]";
    }

    public abstract String getRawUsage();
}
