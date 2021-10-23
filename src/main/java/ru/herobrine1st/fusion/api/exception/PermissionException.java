package ru.herobrine1st.fusion.api.exception;

public class PermissionException extends CommandException {
    private final String requirements;

    public PermissionException(String message, String requirements) {
        super(message);
        this.requirements = requirements;
    }

    public String getRequirements() {
        return requirements;
    }
}
