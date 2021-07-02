package ru.herobrine1st.fusion.internal.command.context;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.build.FusionBaseCommand;
import ru.herobrine1st.fusion.internal.listener.ButtonInteractionHandler;

public class SlashCommandContext extends AbstractCommandContextImpl {
    private static final Logger logger = LoggerFactory.getLogger(SlashCommandContext.class);
    public SlashCommandContext(Event event, FusionBaseCommand<?> command) {
        super(event, command);
    }

    @Override
    RestAction<Message> handleReply(Message message) {
        if(!(event instanceof SlashCommandEvent slashCommandEvent)) throw new IllegalStateException();
        return slashCommandEvent.getHook().sendMessage(message);
    }
}
