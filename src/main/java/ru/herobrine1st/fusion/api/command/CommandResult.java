package ru.herobrine1st.fusion.api.command;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Nullable;

public record CommandResult(MessageEmbed embed, int successCount,
                            int totalCount) {
    public static CommandResult success(MessageEmbed embed) {
        return new Builder().embed(embed).successCount(1).build();
    }

    public static CommandResult failure(MessageEmbed embed) {
        return new Builder().embed(embed).totalCount(1).build();
    }


    public static class Builder {
        private MessageEmbed embed = null;
        private int successCount = 0;
        private int totalCount = 0;

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
            Checks.notNull(embed, "Embed cannot be null");
            return new CommandResult(embed, successCount, totalCount);
        }
    }
}
