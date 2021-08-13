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

public class StringParserElement extends ChoicesParserElement<StringParserElement, String> {
    private final boolean joinRemaining;
    private final boolean breakOnNewLine;
    private final boolean canBeEmpty;

    public StringParserElement(String name, String description, boolean joinRemaining, boolean breakOnNewLine, boolean canBeEmpty) {
        super(name, description);
        this.joinRemaining = joinRemaining;
        this.breakOnNewLine = breakOnNewLine;
        this.canBeEmpty = canBeEmpty;
        setRequired(!canBeEmpty);
    }

    @Override
    public String parseValue(@NotNull CommandArgs args, CommandContext ctx) throws ArgumentParseException {
        if(choices.isEmpty()) {
            if (!args.hasNext() && !canBeEmpty) throw new NoSuchElementException();
            StringBuilder builder = new StringBuilder();
            while (args.hasNext()) {
                String value = args.next().getValue();
                builder.append(value);
                if (breakOnNewLine && value.contains("\n")) break;
                if (joinRemaining) break;
                if (args.hasNext()) builder.append(" ");
            }
            return builder.toString();
        } else {
            String value = choices.get(args.next().getValue());
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
        OptionData optionData = new OptionData(OptionType.STRING, getName(), getDescription(), required);
        for(Map.Entry<String, String> entry: choices.entrySet()) {
            optionData.addChoice(entry.getKey(), entry.getValue());
        }
        return optionData;
    }

    @Override
    public String parseSlash(CommandContext ctx, @NotNull CommandInteraction interaction) {
        OptionMapping optionMapping = interaction.getOption(getName());
        if (optionMapping == null) throw new NoSuchElementException();
        return optionMapping.getAsString();
    }

    @Override
    public String getRawUsage() {
        if (choices.isEmpty())
            return getName() + (joinRemaining ? "..." : "");
        else return String.join("|", choices.keySet());
    }
}
