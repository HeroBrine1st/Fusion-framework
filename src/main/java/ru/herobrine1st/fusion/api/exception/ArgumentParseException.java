package ru.herobrine1st.fusion.api.exception;

import ru.herobrine1st.fusion.api.command.args.CommandArgs;
import ru.herobrine1st.fusion.api.command.args.SingleArg;

public class ArgumentParseException extends CommandException {
    public ArgumentParseException(String message) {
        super(message);
    }

    public static ArgumentParseException withPointer(String message, CommandArgs args) {
        if(args.getSource().contains("\n")) return new ArgumentParseException(message);
        SingleArg current = args.current();
        return new ArgumentParseException(
                String.format(
                        "%s\n```\n%s\n%s%s```",
                        message,
                        args.getSource().replace("`", "\\`"),
                        " ".repeat(Math.max(0, current.getStart())),
                        "^".repeat(Math.max(0, current.getEnd() - current.getStart() + 1))
                )
        );

    }
}
