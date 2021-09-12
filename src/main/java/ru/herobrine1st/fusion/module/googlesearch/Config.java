package ru.herobrine1st.fusion.module.googlesearch;

public final class Config {
    private Config() {}

    public static String getKey() {
        return System.getenv("GOOGLESEARCH_key");
    }

    public static String getCX() {
        return System.getenv("GOOGLESEARCH_cx");
    }
}
