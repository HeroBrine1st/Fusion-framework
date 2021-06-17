package ru.herobrine1st.fusion.api.command;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

public record CommandResult(@Nullable MessageEmbed embed, int successCount,
                            int totalCount) {
    private static final CommandResult SUCCESS = new Builder().successCount(1).build();
    private static final CommandResult EMPTY = new Builder().build();
    private static final CommandResult FAILURE = new Builder().totalCount(1).build();

    public static CommandResult success() {
        return SUCCESS;
    }

    public static CommandResult success(MessageEmbed embed) {
        return new Builder().embed(embed).successCount(1).build();
    }

    public static CommandResult failure() {
        return FAILURE;
    }

    public static CommandResult failure(MessageEmbed embed) {
        return new Builder().embed(embed).totalCount(1).build();
    }

    public static CommandResult empty() {
        return EMPTY;
    }

    public static class Builder {
        public MessageEmbed embed = null;
        public int successCount = 0;
        public int totalCount = 0;

        public Builder successCount(int count) {
            successCount = count;
            return this;
        }

        public Builder totalCount(int count) {
            totalCount = count;
            return this;
        }

        public Builder embed(MessageEmbed embed) {
            this.embed = embed;
            return this;
        }

        public CommandResult build() {
            return new CommandResult(embed, successCount, totalCount);
        }
    }
}
