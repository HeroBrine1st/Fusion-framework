package ru.herobrine1st.fusion.api.command.args.parser;

import net.dv8tion.jda.internal.utils.Checks;

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
    public T addChoice(String name, R value) {
        Checks.notEmpty(name, "Name");
        Checks.notLonger(name, 100, "Name");
        Checks.check(choices.size() < 25, "Cannot have more than 25 choices for an option!");
        choices.put(name, value);
        return (T) this;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T addChoices(Map<String, R> choices) {
        choices.forEach(this::addChoice);
        return (T) this;
    }

    @Nonnull
    public Map<String, R> getChoices() {
        return choices;
    }
}
