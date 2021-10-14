package ru.herobrine1st.fusion.api.event;

import net.dv8tion.jda.api.JDA;

public class FusionPreInitializationEvent extends FusionEvent {
    public FusionPreInitializationEvent(JDA jda) {
        super(jda);
    }
}
