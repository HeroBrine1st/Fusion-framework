package ru.herobrine1st.fusion.api.event;

import net.dv8tion.jda.api.JDA;

public class FusionInitializationEvent extends FusionEvent {
    public FusionInitializationEvent(JDA jda) {
        super(jda);
    }
}
