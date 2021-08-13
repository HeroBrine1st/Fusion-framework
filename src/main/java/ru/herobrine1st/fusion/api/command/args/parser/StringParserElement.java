package ru.herobrine1st.fusion.api.command.args.parser;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.args.CommandArgs;
import ru.herobrine1st.fusion.api.command.args.ParserElement;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class StringParserElement extends ParserElement<StringParserElement, String> {
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
    public String parseValue(CommandArgs args, CommandContext ctx) {
        StringBuilder builder = new StringBuilder();
        if (!args.hasNext() && !canBeEmpty) throw new NoSuchElementException();
        while (args.hasNext()) {
            String value = args.next().getValue();
            builder.append(value);
            if (breakOnNewLine && value.contains("\n")) break;
            if (joinRemaining) break;
            if (args.hasNext()) builder.append(" ");
        }
        return builder.toString();
    }

    @Override
    public boolean hasSlashSupport() {
        return true;
    }

    @Override
    public OptionData getOptionData() {
        return new OptionData(OptionType.STRING, getName(), getDescription(), required);
    }

    @Override
    public String parseSlash(CommandContext ctx, CommandInteraction interaction) {
        if (interaction.getOptionsByName(getName()).isEmpty()) throw new NoSuchElementException();
        return interaction.getOptionsByName(getName())
                .stream().map(OptionMapping::getAsString).collect(Collectors.joining(" "));
    }

    @Override
    public String getRawUsage() {
        return getName() + (joinRemaining ? "..." : "");
    }
}
