package ru.herobrine1st.fusion.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static final String DISABLE_MODULE_PREFIX = "DISABLE_MODULE_";

    private Config() {
    }

    public String getToken() {
        return System.getenv("DISCORD_TOKEN");
    }

    public String getModuleSearchPrefix() {
        var prefix = Objects.requireNonNullElse(System.getenv("MODULE_SEARCH_PREFIX"), "ru.herobrine1st.fusion");
        logger.trace("Search prefix: " + prefix);
        return prefix;
    }

    public String getDiscordPrefix() {
        return Objects.requireNonNullElse(System.getenv("DISCORD_PREFIX"), "/");
    }


    private List<String> disabledModules = null;

    public List<String> getDisabledModules() {
        if (disabledModules == null) {
            disabledModules = System.getenv().entrySet().stream()
                    .filter(it -> it.getKey().startsWith(DISABLE_MODULE_PREFIX))
                    .filter(it -> Boolean.parseBoolean(it.getValue()))
                    .map(it -> it.getKey().substring(DISABLE_MODULE_PREFIX.length()))
                    .map(String::toLowerCase)
                    .toList();
        }
        return disabledModules;
    }

    public static final Config INSTANCE = new Config();
}
