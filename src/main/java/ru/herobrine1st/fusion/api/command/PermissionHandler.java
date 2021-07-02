package ru.herobrine1st.fusion.api.command;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PermissionHandler {
    public static PermissionHandler DEFAULT = new Default();

    private static class Default extends PermissionHandler {
        @Override
        public boolean shouldBeFound(Guild guild) {
            return true;
        }

        @Override
        public boolean shouldBeExecuted(CommandContext ctx) {
            return true;
        }

        @Override
        public @NotNull String requirements(CommandContext ctx) {
            return "";
        }

        @Override
        public CommandType commandType() {
            return CommandType.ALL;
        }
    }

    public static class Typed extends PermissionHandler {

        private final CommandType commandType;

        public Typed(CommandType commandType) {
            this.commandType = commandType;
        }

        @Override
        public boolean shouldBeFound(Guild guild) {
            return true;
        }

        @Override
        public boolean shouldBeExecuted(CommandContext ctx) {
            return true;
        }

        @Override
        public @NotNull String requirements(CommandContext ctx) {
            return "";
        }

        @Override
        public CommandType commandType() {
            return commandType;
        }
    }

    public enum CommandType {
        /**
         * Slash command type
         */
        SLASH,
        /**
         * Classic message command type
         */
        MESSAGE,
        /**
         * All possible cases
         */
        ALL;

        /**
         * Whether this CommandType tells that command can be executed as slash command
         * @return true if and only if slash execution permitted
         */
        public boolean slashExecutionPermitted() {
            return this != MESSAGE;
        }

        /**
         * Whether this CommandType tells that command can be executed as classic command
         * @return true if and only if classic execution permitted
         */
        public boolean classicExecutionPermitted() {
            return this != SLASH;
        }
    }

    /**
     * Called before context creation. Interrupts command handling with no detailed message if returned false.
     *
     * @param guild Guild which command called on. Null if called in DM
     * @return true if command handling should go on, otherwise false
     */
    public abstract boolean shouldBeFound(@Nullable Guild guild);

    /**
     * Called before execution. Interrupts command handling with detailed {@link #requirements(CommandContext) message} if returned false.
     *
     * @param ctx Execution context
     * @return true if command should be executed, otherwise false
     */
    public abstract boolean shouldBeExecuted(CommandContext ctx);

    /**
     * Requirements message, for example a list of discord permissions.
     *
     * @param ctx Execution context
     * @return Requirements message
     */
    public abstract @NotNull String requirements(CommandContext ctx);

    /**
     * Type of command.
     *
     * @return {@link CommandType} describing type of command
     */
    public abstract CommandType commandType();
}