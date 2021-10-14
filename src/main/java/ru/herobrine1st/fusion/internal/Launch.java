package ru.herobrine1st.fusion.internal;

import ru.herobrine1st.fusion.api.Fusion;
import ru.herobrine1st.fusion.util.JDAFactory;

import javax.security.auth.login.LoginException;
import java.util.Objects;

public class Launch {
    public static void main(String[] args) throws LoginException, InterruptedException {
        Fusion.start(JDAFactory.createDefault(Objects.requireNonNull(System.getenv("DISCORD_TOKEN"))));
    }
}
