package ru.herobrine1st.fusion.api.exception;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.ArrayList;
import java.util.List;

public class CommandException extends Exception {
    private final List<MessageEmbed.Field> fields = new ArrayList<>();
    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandException addField(MessageEmbed.Field field) {
        Checks.check(fields.size() < 25, "Exceed field count limit");
        fields.add(field);
        return this;
    }

    public CommandException addField(String name, String value, boolean inline) {
        return addField(new MessageEmbed.Field(name, value, inline));
    }

    public List<MessageEmbed.Field> getFields() {
        return fields;
    }
}
