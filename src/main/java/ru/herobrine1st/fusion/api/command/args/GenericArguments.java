package ru.herobrine1st.fusion.api.command.args;

import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.exception.ArgumentParseException;
import ru.herobrine1st.fusion.internal.command.args.CommandArgs;
import ru.herobrine1st.fusion.internal.command.args.SingleArg;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class GenericArguments {
    private static final Map<String, Boolean> booleanChoices = new HashMap<>(6, 1);

    static {
        booleanChoices.put("true", true);
        booleanChoices.put("false", false);
        booleanChoices.put("yes", true);
        booleanChoices.put("no", false);
        booleanChoices.put("y", true);
        booleanChoices.put("n", false);
    }

    private GenericArguments() {
    }

    /**
     * Оставшиеся соединенные строки. Собирает все оставшиеся строки в одну строку и отправляет в контекст под ключом key
     * Не имеет backtracking. Всегда что-то возвращает.
     *
     * @param key         ключ, которым результат будет отображаться в контексте
     * @param description Описание аргумента
     */
    public static ParserElement remainingJoinedStrings(String key, String description) {
        return new RemainingJoinedStringsParserElement(key, description, false);
    }

    /**
     * @param key            ключ, которым результат будет отображаться в контексте
     * @param description    Описание аргумента
     * @param breakOnNewLine если true, прекращает сборку строки при достижении \n (при этом \n будет добавлен в строку)
     * @see GenericArguments#remainingJoinedStrings(java.lang.String, java.lang.String)
     */
    public static ParserElement remainingJoinedStrings(String key, String description, boolean breakOnNewLine) {
        return new RemainingJoinedStringsParserElement(key, description, breakOnNewLine);
    }

    /**
     * Парсит ключ вида --ключ=значение, -одиночные_символы, --ключ="значение с пробелом"
     * Отправляет значение в element, который распарсит его
     *
     * @param element элемент, который будет парсить содержимое ключа
     */
    public static ParserElement key(ParserElement element) {
        return new KeyParserElement(element, true);
    }

    /**
     * @param optional если true, ключ становится опциональным и не вызывает ошибок при отсутствии
     * @see GenericArguments#key(ru.herobrine1st.fusion.api.command.args.ParserElement)
     */
    public static ParserElement key(ParserElement element, boolean optional) {
        return new KeyParserElement(element, optional);
    }

    /**
     * Делает абсолютно любой элемент опциональным
     *
     * @param element элемент, который нужно сделать опциональным
     */
    public static ParserElement optional(ParserElement element) {
        return new OptionalParserElement(element, null);
    }

    /**
     * @param defaultValue значение по умолчанию
     * @see ru.herobrine1st.fusion.api.command.args.GenericArguments#optional(ru.herobrine1st.fusion.api.command.args.ParserElement)
     */
    public static ParserElement optional(ParserElement element, Object defaultValue) {
        return new OptionalParserElement(element, defaultValue);
    }

    /**
     * Позволяет пользователю выбрать из списка
     *
     * @param key         ключ, которым результат будет отображаться в контексте
     * @param description Описание аргумента
     * @param choices     Выбор пользователя. Ключ карты — то, что должен ввести пользователь, значение — то, что получит команда
     */
    public static ParserElement choices(String key, String description, Map<String, ?> choices) {
        return new ChoicesParserElement(key, description, choices);
    }

    /**
     * @param key         ключ, которым результат будет отображаться в контексте
     * @param description Описание аргумента
     * @param choices     Выбор пользователя. Ключ карты — то, что должен ввести пользователь, значение — то, что получит команда
     * @param usage       строка использования, которая будет отображаться пользователю при /help
     * @see ru.herobrine1st.fusion.api.command.args.GenericArguments#choices(java.lang.String, java.lang.String, java.util.Map)
     */
    public static ParserElement choices(String key, String description, Map<String, ?> choices, String usage) {
        return new ChoicesParserElement(key, description, choices, usage);
    }

    /**
     * @param key         ключ, которым результат будет отображаться в контексте
     * @param description Описание аргумента
     * @see ru.herobrine1st.fusion.api.command.args.GenericArguments#choices(java.lang.String, java.lang.String, java.util.Map)
     * Позволяет пользователю выбрать да/нет
     */
    public static ParserElement bool(String key, String description) {
        return new BooleanParserElement(key, description);
    }

    /**
     * Позволяет пользователю выбрать да/нет с помощью флага. Если пользователь ничего не выберет, вернется false
     *
     * @param key         ключ, которым результат будет отображаться в контексте
     * @param description Описание аргумента
     * @see ru.herobrine1st.fusion.api.command.args.GenericArguments#key(ru.herobrine1st.fusion.api.command.args.ParserElement)
     */
    public static ParserElement flag(String key, String description) {
        return new FlagParserElement(key, description);
    }

    /**
     * Одиночная строка
     *
     * @param key         ключ, которым результат будет отображаться в контексте
     * @param description Описание аргумента
     */
    public static ParserElement singleString(String key, String description) {
        return new SingleStringParserElement(key, description);
    }

    public static ParserElement singleString(String key, String description, int maxLength) {
        return new SingleStringParserElement(key, description, maxLength);
    }

    /**
     * Изменяет строку использования элемента
     *
     * @param element  элемент
     * @param rawUsage новая строка использования
     */
    public static ParserElement changeRawUsage(ParserElement element, String rawUsage) {
        return new ChangeUsageParserElement(element, rawUsage);
    }

    /**
     * Упоминание пользователя Discord
     *
     * @param key         ключ, которым результат будет отображаться в контексте
     * @param description Описание аргумента
     */
    public static ParserElement discordMention(String key, String description) {
        return new DiscordMentionParserElement(key, description);
    }

    /**
     * Парсит все оставшиеся аргументы, пока они не закончатся
     *
     * @param element элемент, который будет парсить
     */
    public static ParserElement untilEnds(ParserElement element) {
        return new ParseUntilEndsElement(element);
    }


    /**
     * Парсит все оставшиеся аргументы, пока они не закончатся
     *
     * @param element  элемент, который будет парсить
     * @param minCount минимальное количество итераций
     * @see GenericArguments#untilEnds(ru.herobrine1st.fusion.api.command.args.ParserElement)
     */
    public static ParserElement untilEnds(ParserElement element, int minCount) {
        return new ParseUntilEndsElement(element, minCount);
    }

    static class RemainingJoinedStringsParserElement extends ParserElement {
        private final boolean breakOnNewLine;

        RemainingJoinedStringsParserElement(String key, String description, boolean breakOnNewLine) {
            super(key, description);
            this.breakOnNewLine = breakOnNewLine;
        }

        @Override
        public Object parseValue(CommandArgs args, CommandContext ctx) {
            StringBuilder builder = new StringBuilder();
            if (!args.hasNext()) throw new NoSuchElementException();
            while (args.hasNext()) {
                String value = args.next().getValue();
                if (breakOnNewLine && value.contains("\n")) {
                    builder.append(value);
                    break;
                }
                builder.append(value);
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
            return new OptionData(OptionType.STRING, getKey(), getDescription());
        }

        @Override
        public Object parseSlash(CommandContext ctx, CommandInteraction interaction) {
            return interaction.getOptionsByName(getKey())
                    .stream().map(OptionMapping::getAsString).collect(Collectors.joining(" "));
        }

        @Override
        public String getRawUsage() {
            return getKey() + "...";
        }
    }

    static class KeyParserElement extends ParserElement {
        private final ParserElement element;
        private final boolean optional;

         KeyParserElement(ParserElement element, boolean optional) {
            super(null, null);
            this.element = element;
            this.optional = optional;
        }

        @Override
        public String getKey() {
            return element.getKey();
        }

        @Override
        public String getDescription() {
            return element.getDescription();
        }

        @Override
        public Object parseValue(CommandArgs args, CommandContext ctx) throws ArgumentParseException {
            Optional<CommandArgs> value = args.getKey(getKey());
            if (value.isEmpty())
                if (!optional)
                    throw new ArgumentParseException(String.format("Ключ %s отсутствует", getKey()));
                else
                    return null;
            return element.parseValue(value.get(), ctx);
        }

        @Override
        public boolean hasSlashSupport() {
            return element.hasSlashSupport();
        }

        @Override
        public OptionData getOptionData() {
            return new OptionData(
                    element.getOptionData().getType(),
                    getKey(), getDescription(), optional);
        }

        @Override
        public void parseSlash(CommandContext ctx) throws ArgumentParseException {
             try {
                 element.parseSlash(ctx);
             } catch (NoSuchElementException | ArgumentParseException e) {
                 if(!optional) throw e;
             }
        }

        @Override
        public Object parseSlash(CommandContext ctx, CommandInteraction interaction) {
            return null;
        }

        @Override
        public String getUsage() {
            return optional ? "[" + getRawUsage() + "]" : getRawUsage();
        }

        @Override
        public String getRawUsage() {
            return "--" + getKey() + "=" + element.getRawUsage();
        }
    }

    static class OptionalParserElement extends ParserElement {
        private final ParserElement element;
        private final Object defaultValue;

        OptionalParserElement(ParserElement element, Object defaultValue) {
            super(element.getKey(), element.getDescription());
            this.element = element;
            this.defaultValue = defaultValue;
        }

        @Override
        public void parse(CommandArgs args, CommandContext ctx) {
            int pos = args.getPos();
            try {
                element.parse(args, ctx);
            } catch (ArgumentParseException | NoSuchElementException e) {
                if (defaultValue != null)
                    ctx.putArg(element.getKey(), defaultValue);
                args.setPos(pos);
            }
        }

        @Override
        public Object parseValue(CommandArgs args, CommandContext ctx) {
            return null;
        }

        @Override
        public boolean hasSlashSupport() {
            return element.hasSlashSupport();
        }

        @Override
        public OptionData getOptionData() {
            return element.getOptionData().setRequired(false);
        }

        @Override
        public void parseSlash(CommandContext ctx) {
            try {
                element.parseSlash(ctx);
            } catch (ArgumentParseException | NoSuchElementException e) {
                if (defaultValue != null)
                    ctx.putArg(element.getKey(), defaultValue);
            }
        }

        @Override
        public Object parseSlash(CommandContext ctx, CommandInteraction interaction) {
            return null;
        }

        @Override
        public String getUsage() {
            return "[" + getRawUsage() + "]";
        }

        @Override
        public String getRawUsage() {
            return element.getRawUsage();
        }
    }

    static class ChoicesParserElement extends ParserElement {
        private final Map<String, ?> choices;
        private final String usage;

        public ChoicesParserElement(String key, String description, Map<String, ?> choices) {
            super(key, description);
            this.choices = choices;
            StringBuilder builder = new StringBuilder();

            for (Iterator<? extends Map.Entry<String, ?>> iterator = choices.entrySet().iterator(); iterator.hasNext(); ) {
                builder.append(iterator.next().getKey());
                if (iterator.hasNext()) builder.append("|");
            }
            usage = builder.toString();
        }

        public ChoicesParserElement(String key, String description, Map<String, ?> choices, String usage) {
            super(key, description);
            this.choices = choices;
            this.usage = usage;
        }

        @Override
        protected Object parseValue(CommandArgs args, CommandContext ctx) throws ArgumentParseException {
            SingleArg arg = args.next();
            return choices.get(
                    choices.keySet().stream().filter((String s) -> s.equalsIgnoreCase(arg.getValue())).findAny()
                            .orElseThrow(() -> ArgumentParseException.withPointer("Аргумент " + getKey() + " не распознан", args))
            );

        }

        @Override
        public boolean hasSlashSupport() {
            return true;
        }

        @Override
        public OptionData getOptionData() {
            var optionData = new OptionData(OptionType.STRING, getKey(), getDescription(), true);
            for (var choice : choices.entrySet()) {
                optionData.addChoice(choice.getKey(), choice.getKey());
            }
            return optionData;
        }

        @Override
        public Object parseSlash(CommandContext ctx, CommandInteraction interaction) throws ArgumentParseException {
            var option = interaction.getOption(getKey());
            if (option == null) throw new NoSuchElementException(getKey());
            var value = choices.get(option.getAsString());
            if (value == null) throw new ArgumentParseException("Аргумент %s не распознан".formatted(getKey()));
            return value;
        }

        @Override
        public String getRawUsage() {
            return usage;
        }
    }

    static class SingleStringParserElement extends ParserElement {

        private final int maxLength;

        protected SingleStringParserElement(String key, String description) {
            this(key, description, Integer.MAX_VALUE);
        }

        protected SingleStringParserElement(String key, String description, int maxLength) {
            super(key, description);
            this.maxLength = maxLength;
        }

        @Override
        protected Object parseValue(CommandArgs args, CommandContext ctx) throws ArgumentParseException {
            String s = args.next().getValue();
            if (s.length() > maxLength)
                throw ArgumentParseException.withPointer(String.format("Превышена максимальная длина %s символов", maxLength), args);
            return s;
        }

        @Override
        public boolean hasSlashSupport() {
            return true;
        }

        @Override
        public OptionData getOptionData() {
            return new OptionData(OptionType.STRING, getKey(), getDescription());
        }

        @Override
        public Object parseSlash(CommandContext ctx, CommandInteraction interaction) throws ArgumentParseException {
            var value = interaction.getOptionsByName(getKey())
                    .stream().map(OptionMapping::getAsString).collect(Collectors.joining(" "));
            if (value.length() > maxLength)
                throw new ArgumentParseException("Превышена максимальная длина %d символов у аргумента %s".formatted(maxLength, getKey()));
            return value;
        }

        @Override
        public String getRawUsage() {
            return getKey();
        }
    }

    static class ChangeUsageParserElement extends ParserElement {
        private final ParserElement element;
        private final String rawUsage;

        protected ChangeUsageParserElement(ParserElement element, String rawUsage) {
            super(null, null);
            this.element = element;
            this.rawUsage = rawUsage;
        }

        @Override
        public void parse(CommandArgs args, CommandContext ctx) throws ArgumentParseException {
            element.parse(args, ctx);
        }

        @Override
        public String getKey() {
            return element.getKey();
        }

        @Override
        public String getDescription() {
            return element.getDescription();
        }

        @Override
        protected Object parseValue(CommandArgs args, CommandContext ctx) {
            return null;
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
        public Object parseSlash(CommandContext ctx, CommandInteraction interaction) {
            return null;
        }

        @Override
        public String getRawUsage() {
            return rawUsage;
        }
    }

    static class DiscordMentionParserElement extends ParserElement {
        private final static String mentionRegex = "<@!?(\\d+)>";
        private final static Pattern mentionPattern = Pattern.compile(mentionRegex);

        protected DiscordMentionParserElement(String key, String description) {
            super(key, description);
        }

        @Override
        protected Object parseValue(CommandArgs args, CommandContext ctx) throws ArgumentParseException {
            String arg = args.next().getValue();
            Matcher mentionMatcher = mentionPattern.matcher(arg);
            if (!mentionMatcher.find()) {
                throw ArgumentParseException.withPointer("Аргумент " + getKey() + " не распознан", args);
            }
            return ctx.getJDA().getUserById(mentionMatcher.group(1));
        }

        @Override
        public boolean hasSlashSupport() {
            return true;
        }

        @Override
        public OptionData getOptionData() {
            return new OptionData(OptionType.USER, getKey(), getDescription(), true);
        }

        @Override
        public Object parseSlash(CommandContext ctx, CommandInteraction interaction) {
            var value = interaction.getOption(getKey());
            if (value == null) throw new NoSuchElementException();
            return value.getAsUser();
        }

        @Override
        public String getUsage() {
            return getRawUsage();
        }

        @Override
        public String getRawUsage() {
            return "@mention";
        }
    }

    static class ParseUntilEndsElement extends ParserElement {

        private final ParserElement element;
        private final int minCount;

        protected ParseUntilEndsElement(ParserElement element) {
            this(element, 1);
        }


        protected ParseUntilEndsElement(ParserElement element, int minCount) {
            super(null, null);
            this.element = element;
            this.minCount = minCount;
        }

        @Override
        public String getKey() {
            return element.getKey();
        }

        @Override
        public String getDescription() {
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
        protected Object parseValue(CommandArgs args, CommandContext ctx) {
            return null;
        }

        @Override
        public boolean hasSlashSupport() {
            return false;
        }

        @Override
        public OptionData getOptionData() {
            return null;
        }

        @Override
        public Object parseSlash(CommandContext ctx, CommandInteraction interaction) {
            return null;
        }

        @Override
        public String getRawUsage() {
            return null;
        }

        @Override
        public String getUsage() {
            return element.getUsage() + "...";
        }
    }

    // Вот эти два класса - костыли, но если хочется кому-то их презентовать, то говорите, что это - обертки
    static class FlagParserElement extends ParserElement { // Обертка, потому что по отдельности все три элемента поддерживают слеш-команды, но в итоге получается полная хуйня
        private final ParserElement element;

        protected FlagParserElement(String key, String description) {
            super(key, description);
            this.element = GenericArguments.optional(
                    GenericArguments.key(
                            GenericArguments.choices(key, description, booleanChoices, "bool"), false),
                    false);
        }

        @Override
        public void parse(CommandArgs args, CommandContext ctx) throws ArgumentParseException {
            element.parse(args, ctx);
        }

        @Override
        protected Object parseValue(CommandArgs args, CommandContext ctx) {
            return null;
        }

        @Override
        public boolean hasSlashSupport() {
            return true;
        }

        @Override
        public OptionData getOptionData() {
            return new OptionData(OptionType.BOOLEAN, getKey(), getDescription(), false);
        }

        @Override
        public Object parseSlash(CommandContext ctx, CommandInteraction interaction) {
            var value = interaction.getOption(getKey());
            if (value == null) return false;
            return value.getAsBoolean();
        }

        @Override
        public String getRawUsage() {
            return "-" + (getKey().length() > 1 ? "-" : "") + getKey();
        }
    }

    static class BooleanParserElement extends ParserElement {
        private final ParserElement element;

        protected BooleanParserElement(String key, String description) {
            super(key, description);
            this.element = GenericArguments.choices(key, description, booleanChoices);
        }

        @Override
        public void parse(CommandArgs args, CommandContext ctx) throws ArgumentParseException {
            element.parse(args, ctx);
        }

        @Override
        protected Object parseValue(CommandArgs args, CommandContext ctx) {
            return null;
        }

        @Override
        public boolean hasSlashSupport() {
            return true;
        }

        @Override
        public OptionData getOptionData() {
            return new OptionData(OptionType.BOOLEAN, getKey(), getDescription(), true);
        }

        @Override
        public Object parseSlash(CommandContext ctx, CommandInteraction interaction) {
            var value = interaction.getOption(getKey());
            if (value == null) throw new NoSuchElementException(getKey());
            return value.getAsBoolean();
        }

        @Override
        public String getRawUsage() {
            return "boolean";
        }
    }
}
