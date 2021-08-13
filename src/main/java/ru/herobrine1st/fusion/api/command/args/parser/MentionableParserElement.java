package ru.herobrine1st.fusion.api.command.args.parser;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.args.CommandArgs;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;

import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionableParserElement extends ParserElement<MentionableParserElement, IMentionable> {

    private final static String mentionRegex = "<@([!&]?)(\\d+)>";
    private final static Pattern mentionPattern = Pattern.compile(mentionRegex);

    public MentionableParserElement(String name, String description) {
        super(name, description);
    }

    @Override
    public IMentionable parseValue(@NotNull CommandArgs args, CommandContext ctx) throws ArgumentParseException {
        String arg = args.next().getValue();
        Matcher mentionMatcher = mentionPattern.matcher(arg);
        if (!mentionMatcher.find()) {
            throw ArgumentParseException.withPointer("Аргумент " + getName() + " не распознан", args);
        }
        if (mentionMatcher.group(1).equals("&")) {
            // This method is called only on "classic" message commands
            Role role = ctx.getMessage().orElseThrow().getGuild().getRoleById(mentionMatcher.group(1));
            if(role == null)
                throw ArgumentParseException.withPointer("Аргумент " + getName() + " не может быть реплицирован в роль", args);
            return role;
        } else { // "!" or ""
            User user = ctx.getJDA().getUserById(mentionMatcher.group(1));
            if(user == null)
                throw ArgumentParseException.withPointer("Аргумент " + getName() + " не может быть реплицирован в пользователя: пользователя нет в кеше бота", args);
            return user;
        }
    }

    @Override
    public boolean hasSlashSupport() {
        return true;
    }

    @Override
    public OptionData getOptionData() {
        return new OptionData(OptionType.MENTIONABLE, name, description, required);
    }

    @Override
    public IMentionable parseSlash(CommandContext ctx, @NotNull CommandInteraction interaction) {
        OptionMapping option = interaction.getOption(name);
        if(option == null) throw new NoSuchElementException();
        return option.getAsMentionable();
    }

    @Override
    public String getRawUsage() {
        return "mentionable";
    }
}
