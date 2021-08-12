package ru.herobrine1st.fusion.api.command.args.parser;

public class FlagParserElement extends KeyParserElement<Boolean> {
    public FlagParserElement(String name, String description) {
        super(new BooleanParserElement(name, description), false);
    }
}
