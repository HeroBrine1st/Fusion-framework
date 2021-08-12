package ru.herobrine1st.fusion.api.command.args;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.args.parser.*;

/**
 * Class containing various {@link ParserElement} provider methods
 * <h2><a id="message-context">Message context</a></h2>
 * Some parsers can function only in message context. They will disable application commands feature in every command that contains any of them.
 */
public final class GenericArguments {

    private GenericArguments() {
    }

    /**
     * Implementation of STRING discord option type. Supplies values of {@link String} type.
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @see #string(String, String, boolean, boolean, boolean)
     */
    @Contract("_, _ -> new")
    public static @NotNull ParserElement<?, String> string(String name, String description) {
        return new StringParserElement(name, description, false, false, false);
    }

    /**
     * Implementation of STRING discord option type. Supplies values of {@link String} type.
     * <br>
     * Additional arguments work only in <a href="#message-context">message context</a>
     *
     * @param name          name of the option.
     * @param description   description of the option.
     * @param joinRemaining if true, will cyclically join all remaining arguments provided by user.
     * @see #string(String, String, boolean, boolean, boolean)
     */
    @Contract("_, _, _ -> new")
    public static @NotNull ParserElement<?, String> string(String name, String description, boolean joinRemaining) {
        return new StringParserElement(name, description, joinRemaining, false, false);
    }

    /**
     * Implementation of STRING discord option type. Supplies values of {@link String} type.
     * <br>
     * Additional arguments work only in <a href="#message-context">message context</a>
     *
     * @param name           name of the option.
     * @param description    description of the option.
     * @param joinRemaining  if true, will cyclically join all remaining arguments provided by user.
     * @param breakOnNewLine if this and joinRemaining are true, will cyclically join all remaining arguments until newline separator.
     * @see #string(String, String, boolean, boolean, boolean)
     */
    @Contract("_, _, _, _ -> new")
    public static @NotNull ParserElement<?, String> string(String name, String description, boolean joinRemaining, boolean breakOnNewLine) {
        return new StringParserElement(name, description, joinRemaining, breakOnNewLine, false);
    }

    /**
     * Implementation of STRING discord option type. Supplies values of {@link String} type.
     * <br>
     * Additional arguments work only in <a href="#message-context">message context</a>
     *
     * @param name           name of the option.
     * @param description    description of the option.
     * @param joinRemaining  if true, will cyclically join all remaining arguments provided by user.
     * @param breakOnNewLine if this and joinRemaining are true, will cyclically join all remaining arguments until newline separator.
     * @param canBeEmpty     if false, will throw an exception if no arguments remaining. If true, will become optional.
     * @return {@link StringParserElement} instance
     */
    @Contract("_, _, _, _, _ -> new")
    public static @NotNull ParserElement<?, String> string(String name, String description, boolean joinRemaining, boolean breakOnNewLine, boolean canBeEmpty) {
        return new StringParserElement(name, description, joinRemaining, breakOnNewLine, canBeEmpty);
    }

    /**
     * Implementation of INTEGER discord option type. Supplies values of {@link Long} type
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @return {@link IntegerParserElement} instance
     * @see #integer(String, String, int, int, int)
     */
    @Contract("_, _ -> new")
    public static @NotNull ParserElement<?, Long> integer(String name, String description) {
        return new IntegerParserElement(name, description, Long.MIN_VALUE, Long.MAX_VALUE, 10);
    }

    /**
     * Implementation of INTEGER discord option type. Supplies values of {@link Long} type
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @param min         minimal value of integer
     * @return {@link IntegerParserElement} instance
     * @see #integer(String, String, int, int, int)
     */
    @Contract("_, _, _ -> new")
    public static @NotNull ParserElement<?, Long> integer(String name, String description, long min) {
        return new IntegerParserElement(name, description, min, Long.MAX_VALUE, 10);
    }

    /**
     * Implementation of INTEGER discord option type. Supplies values of {@link Long} type
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @param min         minimal value of integer
     * @param max         maximum value of integer
     * @return {@link IntegerParserElement} instance
     * @see #integer(String, String, int, int, int)
     */
    @Contract("_, _, _, _ -> new")
    public static @NotNull ParserElement<?, Long> integer(String name, String description, long min, long max) {
        return new IntegerParserElement(name, description, min, max, 10);
    }

    /**
     * Implementation of INTEGER discord option type. Supplies values of {@link Long} type
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @param min         minimal value of integer
     * @param max         maximum value of integer
     * @param radix       radix used when parsing integer in <a href="#message-context">message context</a>
     * @return {@link IntegerParserElement} instance
     */
    @Contract("_, _, _, _, _ -> new")
    public static @NotNull ParserElement<?, Long> integer(String name, String description, int min, int max, int radix) {
        return new IntegerParserElement(name, description, min, max, radix);
    }

    /**
     * Implementation of BOOLEAN discord option type. Supplies values of {@link Boolean} type
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @return {@link BooleanParserElement} instance
     */
    @Contract("_, _ -> new")
    public static @NotNull ParserElement<?, Boolean> bool(String name, String description) {
        return new BooleanParserElement(name, description);
    }

    /**
     * Implementation of USER discord option type. Supplies values of {@link net.dv8tion.jda.api.entities.User User} type
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @return {@link UserParserElement} instance
     */
    @Contract("_, _ -> new")
    public static @NotNull ParserElement<?, User> user(String name, String description) {
        return new UserParserElement(name, description);
    }

    /**
     * Implementation of CHANNEL discord option type. Supplies values of {@link net.dv8tion.jda.api.entities.GuildChannel GuildChannel} type
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @return {@link ChannelParserElement} instance
     */
    @Contract("_, _ -> new")
    public static @NotNull ParserElement<?, GuildChannel> channel(String name, String description) {
        return new ChannelParserElement(name, description);
    }

    /**
     * Implementation of ROLE discord option type. Supplies values of {@link net.dv8tion.jda.api.entities.Role Role} type
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @return {@link RoleParserElement} instance
     */
    @Contract("_, _ -> new")
    public static @NotNull ParserElement<?, Role> role(String name, String description) {
        return new RoleParserElement(name, description);
    }

    /**
     * Implementation of MENTIONABLE discord option type. Supplies values of {@link net.dv8tion.jda.api.entities.IMentionable IMentionable} type
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @return {@link MentionableParserElement} instance
     */
    @Contract("_, _ -> new")
    public static @NotNull ParserElement<?, IMentionable> mentionable(String name, String description) {
        return new MentionableParserElement(name, description);
    }

    /**
     * Wrapper around any ParserElement. Allows using option as {@code --name=value}: {@code name} is name of element, {@code value} is user input.
     *
     * @param element element inside wrapper
     * @param <T>     Parse result type.
     * @return {@link KeyParserElement} instance
     */
    @Contract("_ -> new")
    public static <T> @NotNull ParserElement<?, T> key(ParserElement<?, T> element) {
        return new KeyParserElement<>(element, null);
    }

    /**
     * Wrapper around any ParserElement. Allows using option as {@code --name=value}: {@code name} is name of element, {@code value} is user input.
     *
     * @param element element inside wrapper
     * @param <T>     Parse result type.
     * @return {@link KeyParserElement} instance
     */
    @Contract("_, _ -> new")
    public static <T> @NotNull ParserElement<?, T> key(ParserElement<?, T> element, T defaultValue) {
        return new KeyParserElement<>(element, defaultValue);
    }

    /**
     * Wrapper around BooleanParserElement. Allows using as {@code --flag} (flag {@code flag} will be set to true) or even {@code -abc} (flags {@code a}, {@code b} and {@code c} will be set to true)
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @return {@link FlagParserElement} instance
     */
    @Contract("_, _ -> new")
    public static @NotNull ParserElement<?, Boolean> flag(String name, String description) {
        return new FlagParserElement(name, description);
    }

    /**
     * Wrapper around any element. Continually parses using provided element until there are remaining arguments.
     *
     * @param element element inside wrapper
     * @param <T>     Parse result type.
     * @return {@link ParseUntilEndsParserElement} instance
     */
    @Contract("_ -> new")
    public static <T> @NotNull ParserElement<?, T> untilEnds(ParserElement<?, T> element) {
        return new ParseUntilEndsParserElement<>(element, 1);
    }

    @Contract("_, _ -> new")
    public static <T> @NotNull ParserElement<?, T> untilEnds(ParserElement<?, T> element, int minCount) {
        return new ParseUntilEndsParserElement<>(element, minCount);
    }
}
