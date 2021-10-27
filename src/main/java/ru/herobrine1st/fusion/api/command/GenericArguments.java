package ru.herobrine1st.fusion.api.command;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.option.parser.*;

/**
 * Class containing various {@link ParserElement} provider methods
 */
public final class GenericArguments {

    private GenericArguments() {
    }

    /**
     * Implementation of STRING discord option type. Supplies values of {@link String} type.
     * <br>
     * Additional arguments work only in <a href="#message-context">message context</a> and without choices
     *
     * @param name          name of the option.
     * @param description   description of the option.
     * @return {@link StringParserElement} instance
     */
    @Contract("_, _ -> new")
    public static @NotNull StringParserElement string(String name, String description) {
        return new StringParserElement(name, description);
    }

    /**
     * Implementation of INTEGER discord option type. Supplies values of {@link Long} type
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @return {@link IntegerParserElement} instance
     * @see #integer(String, String, long, long)
     */
    @Contract("_, _ -> new")
    public static @NotNull IntegerParserElement integer(String name, String description) {
        return new IntegerParserElement(name, description, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * Implementation of INTEGER discord option type. Supplies values of {@link Long} type<br>
     * Additional arguments work only without choices.
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @param min         minimal value of integer
     * @return {@link IntegerParserElement} instance
     * @see #integer(String, String, long, long)
     */
    @Contract("_, _, _ -> new")
    public static @NotNull IntegerParserElement integer(String name, String description, long min) {
        return new IntegerParserElement(name, description, min, Long.MAX_VALUE);
    }

    /**
     * Implementation of INTEGER discord option type. Supplies values of {@link Long} type.<br>
     * Additional arguments work only without choices.
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @param min         minimal value of integer
     * @param max         maximum value of integer
     * @return {@link IntegerParserElement} instance
     * @see #integer(String, String, long, long)
     */
    @Contract("_, _, _, _ -> new")
    public static @NotNull IntegerParserElement integer(String name, String description, long min, long max) {
        return new IntegerParserElement(name, description, min, max);
    }

    /**
     * Implementation of BOOLEAN discord option type. Supplies values of {@link Boolean} type
     *
     * @param name        name of the option.
     * @param description description of the option.
     * @return {@link BooleanParserElement} instance
     */
    @Contract("_, _ -> new")
    public static @NotNull BooleanParserElement bool(String name, String description) {
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
    public static @NotNull UserParserElement user(String name, String description) {
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
    public static @NotNull ChannelParserElement channel(String name, String description) {
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
    public static @NotNull RoleParserElement role(String name, String description) {
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
    public static @NotNull MentionableParserElement mentionable(String name, String description) {
        return new MentionableParserElement(name, description);
    }
}

