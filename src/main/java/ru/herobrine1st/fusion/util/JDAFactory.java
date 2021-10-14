package ru.herobrine1st.fusion.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.internal.Fusion;
import ru.herobrine1st.fusion.internal.listener.ButtonInteractionHandler;
import ru.herobrine1st.fusion.internal.listener.MessageCommandHandler;
import ru.herobrine1st.fusion.internal.listener.SlashCommandHandler;

import javax.security.auth.login.LoginException;

public final class JDAFactory {
    public static @NotNull JDABuilder createBuilder(String token) {
        return JDABuilder.createLight(token, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES)
                .setEventManager(Fusion.INSTANCE.getEventManager())
                .addEventListeners(Fusion.INSTANCE, new MessageCommandHandler(), new SlashCommandHandler(), new ButtonInteractionHandler());
    }

    public static @NotNull JDA createDefault(String token) throws LoginException {
        return createBuilder(token).build();
    }

    private JDAFactory() {}
}
