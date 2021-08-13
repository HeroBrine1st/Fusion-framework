package ru.herobrine1st.fusion.api.command.args.parser;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public abstract class ChoicesParserElement<T, R> extends ParserElement<ChoicesParserElement<T, R>, R> {

    protected final Map<String, R> choices;

    public ChoicesParserElement(String name, String description) {
        super(name, description);
        choices = new HashMap<>();
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T addChoice(String key, R value) {
        choices.put(key, value);
        return (T) this;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T addChoices(Map<String, R> choices) {
        this.choices.putAll(choices);
        return (T) this;
    }

    @Nonnull
    public Map<String, R> getChoices() {
        return choices;
    }
}
