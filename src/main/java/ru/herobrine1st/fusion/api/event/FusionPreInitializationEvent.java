package ru.herobrine1st.fusion.api.event;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

public class FusionPreInitializationEvent extends FusionEvent {
    public FusionPreInitializationEvent() {
        super(null);
    }

    @Override
    public @NotNull JDA getJDA() {
        throw new IllegalStateException();
    }
}
