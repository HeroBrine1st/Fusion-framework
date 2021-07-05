package ru.herobrine1st.fusion.api.event;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

public abstract class FusionEvent implements GenericEvent {
    private final JDA jda;

    public FusionEvent(JDA jda) {
        this.jda = jda;
    }

    @NotNull
    @Override
    public JDA getJDA() {
        return jda;
    }

    @Override
    public long getResponseNumber() {
        return -1;
    }
}
