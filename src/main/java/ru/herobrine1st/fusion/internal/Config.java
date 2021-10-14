package ru.herobrine1st.fusion.internal;

import ru.herobrine1st.fusion.api.Fusion;

import javax.annotation.Nullable;
import java.util.Objects;

public final class Config implements Fusion.Config {
    public String getDiscordPrefix() {
        return Objects.requireNonNullElse(System.getenv("DISCORD_PREFIX"), "/");
    }

    @Nullable
    public String getTestGuildId() {
        return System.getenv("TEST_GUILD_ID");
    }
}
