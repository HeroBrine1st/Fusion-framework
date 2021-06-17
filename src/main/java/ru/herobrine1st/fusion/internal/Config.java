package ru.herobrine1st.fusion.internal;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Config {
    private static final String DISABLE_MODULE_PREFIX = "DISABLE_MODULE_";

    private Config() {
    }

    public String getToken() {
        return System.getenv("DISCORD_TOKEN");
    }

    public String getModuleSearchPrefix() {
        return Objects.requireNonNullElse(System.getenv("MODULE_SEARCH_PREFIX"), "ru.herobrine1st.fusion.module");
    }


    private List<String> disabledModules = null;

    public List<String> getDisabledModules() {
        if (disabledModules == null) {
            disabledModules = System.getenv().entrySet().stream()
                    .filter(it -> it.getKey().startsWith(DISABLE_MODULE_PREFIX))
                    .filter(it -> Boolean.parseBoolean(it.getValue()))
                    .map(it -> it.getKey().substring(DISABLE_MODULE_PREFIX.length()))
                    .toList();
        }
        return disabledModules;
    }

    public static final Config INSTANCE = new Config();
}
