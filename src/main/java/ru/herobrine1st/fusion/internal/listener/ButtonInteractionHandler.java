package ru.herobrine1st.fusion.internal.listener;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.internal.command.context.AbstractCommandContextImpl;

import java.util.HashMap;
import java.util.Map;

public class ButtonInteractionHandler extends ListenerAdapter {
    public final static ButtonInteractionHandler INSTANCE = new ButtonInteractionHandler();
    private final Map<Long, AbstractCommandContextImpl> interactionCache = new HashMap<>();

    private ButtonInteractionHandler() {
    }

    public void open(long messageId, AbstractCommandContextImpl ctx) { // Ну тип открыть для прослушивания
        interactionCache.put(messageId, ctx);
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        // TODO отмена через 5 минут
        if (!interactionCache.containsKey(event.getMessageIdLong())) {
            event.reply("Данное сообщение больше не принимает взаимодействий.").setEphemeral(true).queue();
            return;
        }
        var context = interactionCache.get(event.getMessageIdLong());
        if (context.getUser().getIdLong() != event.getUser().getIdLong()) {
            event.reply("Вы не являетесь автором команды.").setEphemeral(true).queue();
            return;
        }
        if (event.getMessage() != null) {
            if (event.getMessage().getButtonById(event.getComponentId()) == null) {
                return;
            }
        }
        interactionCache.remove(event.getMessageIdLong());
        event.deferReply().queue();
        var hook = event.getHook();
        context.applyButtonClickEvent(event, (message, ctx) -> hook.sendMessage(message));
    }
}
