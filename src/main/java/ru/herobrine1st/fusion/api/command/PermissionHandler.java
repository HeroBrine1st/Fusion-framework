package ru.herobrine1st.fusion.api.command;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.NoSuchElementException;

public abstract class PermissionHandler {
    public final static PermissionHandler DEFAULT = new Default();

    private static class Default extends PermissionHandler {
        @Override
        public boolean shouldNotBeFound(Guild guild) {
            return false;
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
        public boolean shouldNotBeFound(Guild guild) {
            return false;
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

    private static class Accumulator extends PermissionHandler {
        private final PermissionHandler a;
        private final PermissionHandler b;
        private final CommandType commandType;

        public Accumulator(PermissionHandler a, PermissionHandler b) {
            this.a = a;
            this.b = b;
            commandType = a.commandType().and(b.commandType());
        }

        @Override
        public boolean shouldNotBeFound(@Nullable Guild guild) {
            return a.shouldNotBeFound(guild) || b.shouldNotBeFound(guild);
        }

        @Override
        public boolean shouldBeExecuted(CommandContext ctx) {
            return a.shouldBeExecuted(ctx) && b.shouldBeExecuted(ctx);
        }

        @Override
        public @NotNull String requirements(CommandContext ctx) {
            return "%s\n%s".formatted(a.requirements(ctx), b.requirements(ctx));
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
        SLASH(false, true),
        /**
         * Classic message command type
         */
        MESSAGE(true, false),
        /**
         * All possible cases
         */
        ALL(true, true);

        private final boolean messagePermitted;
        private final boolean slashPermitted;

        CommandType(boolean messagePermitted, boolean slashPermitted) {
            this.messagePermitted = messagePermitted;
            this.slashPermitted = slashPermitted;
        }

        CommandType and(CommandType other) {
            return Arrays.stream(CommandType.values())
                    .filter(it -> (it.slashPermitted == this.slashPermitted && other.slashPermitted)
                            && (it.messagePermitted == this.messagePermitted && other.messagePermitted)).findAny()
                    .orElseThrow(() -> new NoSuchElementException("No such CommandType found"));
        }

        /**
         * Whether this CommandType tells that command can be executed as slash command
         *
         * @return true if and only if slash execution permitted
         */
        public boolean slashExecutionPermitted() {
            return slashPermitted;
        }

        /**
         * Whether this CommandType tells that command can be executed as classic command
         *
         * @return true if and only if classic execution permitted
         */
        public boolean classicExecutionPermitted() {
            return messagePermitted;
        }
    }

    /**
     * Called before context creation. Interrupts command handling with no detailed message if returned true.
     *
     * @param guild Guild which command called on. Null if called in DM
     * @return false if command handling should go on, otherwise true
     */
    public abstract boolean shouldNotBeFound(@Nullable Guild guild);

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

    public PermissionHandler and(PermissionHandler other) {
        return new Accumulator(this, other);
    }
}