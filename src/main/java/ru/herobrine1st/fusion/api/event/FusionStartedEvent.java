package ru.herobrine1st.fusion.api.event;

import net.dv8tion.jda.api.JDA;

public class FusionStartedEvent extends FusionEvent {
    public FusionStartedEvent(JDA jda) {
        super(jda);
    }
}
