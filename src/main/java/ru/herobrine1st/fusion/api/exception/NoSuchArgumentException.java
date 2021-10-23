package ru.herobrine1st.fusion.api.exception;

import ru.herobrine1st.fusion.api.command.args.parser.ParserElement;

public class NoSuchArgumentException extends ArgumentParseException {
    private final ParserElement<?, ?> element;

    public NoSuchArgumentException(ParserElement<?, ?> element) {
        super("No argument %s found".formatted(element.getName()));
        this.element = element;
    }

    public ParserElement<?, ?> getElement() {
        return element;
    }
}
