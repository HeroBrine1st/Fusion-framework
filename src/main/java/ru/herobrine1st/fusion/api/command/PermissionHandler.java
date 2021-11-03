package ru.herobrine1st.fusion.api.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public abstract class PermissionHandler {
    public final static PermissionHandler DEFAULT = new Default();

    private static class Default extends PermissionHandler {
        @Override
        public boolean shouldBeExecuted(CommandContext ctx) {
            return true;
        }

        @Override
        public @NotNull String requirements(CommandContext ctx) {
            return "";
        }
    }

    private static class DiscordPermissionHandler extends PermissionHandler {
        private final List<Permission> permissions;

        public DiscordPermissionHandler(List<Permission> permissions) {
            this.permissions = permissions;
        }

        @Override
        public boolean shouldBeExecuted(CommandContext ctx) {
            Member member;
            if((member = ctx.getEvent().getMember()) != null) {
                return member.hasPermission(ctx.getEvent().getGuildChannel(), permissions);
            }
            return false;
        }

        @Override
        public @NotNull String requirements(CommandContext ctx) {
            return permissions.stream()
                    .map(Permission::getName)
                    .collect(Collectors.joining("\n"));
        }
    }

    private static class Accumulator extends PermissionHandler {
        private final PermissionHandler a;
        private final PermissionHandler b;

        public Accumulator(PermissionHandler a, PermissionHandler b) {
            this.a = a;
            this.b = b;
        }


        @Override
        public boolean shouldBeExecuted(CommandContext ctx) {
            return a.shouldBeExecuted(ctx) && b.shouldBeExecuted(ctx);
        }

        @Override
        public @NotNull String requirements(CommandContext ctx) {
            return "%s\n%s".formatted(a.requirements(ctx), b.requirements(ctx));
        }
    }

    public static PermissionHandler discordPermission(List<Permission> permissions) {
        return new DiscordPermissionHandler(permissions);
    }

    public static PermissionHandler discordPermission(Permission... permissions) {
        return discordPermission(List.of(permissions));
    }

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

    public PermissionHandler and(PermissionHandler other) {
        return new Accumulator(this, other);
    }
}