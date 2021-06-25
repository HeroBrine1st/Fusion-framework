package ru.herobrine1st.fusion.api.command.args;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.FusionOptionData;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;

import java.util.NoSuchElementException;

public abstract class ParserElement extends FusionOptionData {

    public ParserElement(String name, String description) {
        super(name, description);
    }

    public void parse(CommandArgs args, CommandContext ctx) throws ArgumentParseException {
        Object value;
        try {
            value = parseValue(args, ctx);
        } catch (NoSuchElementException e) {
            throw ArgumentParseException.withPointer("Обработчик вышел за пределы массива", args);
        }
        if (name != null && value != null) {
            ctx.putArg(name, value);
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
        if(name != null && value != null) {
            ctx.putArg(name, value);
        }
    }

    public abstract Object parseSlash(CommandContext ctx, CommandInteraction interaction) throws ArgumentParseException;

    public String getUsage() {
        return "<" + getRawUsage() + ">";
    }

    public abstract String getRawUsage();
}
