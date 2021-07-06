package ru.herobrine1st.fusion.internal;

import java.util.List;
import java.util.Objects;

public final class Config {
    private static final String DISABLE_MODULE_PREFIX = "DISABLE_MODULE_";
    private final List<String> disabledModules = System.getenv().entrySet().stream()
            .filter(it -> it.getKey().startsWith(DISABLE_MODULE_PREFIX))
            .filter(it -> Boolean.parseBoolean(it.getValue()))
            .map(it -> it.getKey().substring(DISABLE_MODULE_PREFIX.length()))
            .map(String::toLowerCase)
            .toList();

    private Config() {
    }

    public static final Config INSTANCE = new Config();

    public String getToken() {
        return System.getenv("DISCORD_TOKEN");
    }

    public String getModuleSearchPrefix() {
        return Objects.requireNonNullElse(System.getenv("MODULE_SEARCH_PREFIX"), "ru.herobrine1st.fusion");
    }

    public String getDiscordPrefix() {
        return Objects.requireNonNullElse(System.getenv("DISCORD_PREFIX"), "/");
    }


    public List<String> getDisabledModules() {
        return disabledModules;
    }

    public String getDatabaseUrl() {
        return Objects.requireNonNull(System.getenv("DATABASE_URL"), "No DATABASE_URL variable in environment");
    }
}
