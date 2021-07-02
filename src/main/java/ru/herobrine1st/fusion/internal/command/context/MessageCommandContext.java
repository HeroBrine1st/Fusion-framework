package ru.herobrine1st.fusion.internal.command.context;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.build.FusionBaseCommand;

public class MessageCommandContext extends AbstractCommandContextImpl {
    private static final Logger logger = LoggerFactory.getLogger(MessageCommandContext.class);
    public MessageCommandContext(Event event, FusionBaseCommand<?> command) {
        super(event, command);
    }

    @Override
    RestAction<Message> handleReply(Message message) {
        if(!(event instanceof MessageReceivedEvent messageReceivedEvent)) throw new IllegalStateException();
        return messageReceivedEvent.getMessage().reply(message)
                .mentionRepliedUser(false);
    }
}
