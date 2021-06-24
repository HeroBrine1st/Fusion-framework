package ru.herobrine1st.fusion.internal.command.args;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.herobrine1st.fusion.api.command.args.CommandArgs;
import ru.herobrine1st.fusion.api.command.args.ParserElement;
import ru.herobrine1st.fusion.api.command.args.SingleArg;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandArgsImpl implements CommandArgs {
    private static final Pattern regex =
            Pattern.compile("\"([^\"]*)\"|--(\\S+)=\"([^\"]*)\"|--(\\S+)=(\\S+)|--(\\S+)|-(\\S+)|(\\S+\n?)",
                    Pattern.MULTILINE);
    private final String source;
    private final List<SingleArgImpl> args = new ArrayList<>();
    private final Map<String, CommandArgsImpl> keys = new CaseInsensitiveMap<>();
    private int pos = -1;

    public CommandArgsImpl(String str) {
        this(str, true);
    }

    public CommandArgsImpl(String str, boolean parseKeys) {
        source = str;
        Matcher m = regex.matcher(str);
        while (m.find()) {
            if (m.group(1) != null) {
                appendSingleArg(m, 1);
            } else if (m.group(2) != null && m.group(3) != null && parseKeys) {
                processKey(m.group(2), m.group(3));
            } else if (m.group(4) != null && m.group(5) != null && parseKeys) {
                processKey(m.group(4), m.group(5));
            } else if (m.group(6) != null && parseKeys) {
                processKey(m.group(6), null);
            } else if (m.group(7) != null && parseKeys) {
                for (char ch : m.group(7).toCharArray()) {
                    processKey(String.valueOf(ch), null);
                }
            } else if (m.group(8) != null) {
                appendSingleArg(m, 8);
            } else {
                appendSingleArg(m, 0);
            }
        }
    }

    private void appendSingleArg(Matcher m, int group) {
        String value = m.group(group);
        args.add(new SingleArgImpl(m.start(group), m.end(group)-1, value));
    }

    public String getSource() {
        return source;
    }

    // Здесь нужен CommandArgs, что бы элемент, который будет в KeyParserElement, смог парсить переданную в ключ строку
    /**
     * @see ru.herobrine1st.fusion.api.command.args.GenericArguments#key(ParserElement element)
     **/
    private void processKey(String key, @Nullable String value) {
        keys.computeIfAbsent(key, it -> new CommandArgsImpl(Objects.requireNonNullElse(value, "true"), false));
    }

    public Optional<CommandArgsImpl> getKey(String key) {
        return Optional.ofNullable(keys.get(key));
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public boolean hasNext() {
        return args.size() - 1 > pos;
    }

    public SingleArgImpl next() {
        if (!hasNext()) throw new NoSuchElementException();
        return args.get(++pos);
    }

    public SingleArg current() {
        if (pos == -1) throw new NoSuchElementException();
        return args.get(pos);
    }
}
