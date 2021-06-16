package ru.herobrine1st.fusion.api.exception;

public class CommandException extends Exception {
    private boolean includeUsage = false;

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandException(String message, Throwable cause, boolean includeUsage) {
        super(message, cause);
        this.includeUsage = includeUsage;
    }

    public CommandException(String message, boolean includeUsage) {
        super(message);
        this.includeUsage = includeUsage;
    }

    public boolean shouldIncludeUsage() {
        return includeUsage;
    }
}
