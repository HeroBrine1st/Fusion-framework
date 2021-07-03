package ru.herobrine1st.fusion.internal.listener;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.internal.command.CommandContextImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.utils.TimeUtil.*;

public class ButtonInteractionHandler extends ListenerAdapter {
    public final static ButtonInteractionHandler INSTANCE = new ButtonInteractionHandler();
    private final static long TTL = 15 * 60 * 1000;
    private final static Map<Long, CommandContextImpl> interactionCache = new HashMap<>();

    private ButtonInteractionHandler() {
        Executors.newSingleThreadScheduledExecutor(it -> { // TODO вынести в API
            var t = new Thread(it);
            t.setDaemon(true);
            return t;
        }).scheduleAtFixedRate(
                () -> interactionCache.entrySet()
                        .removeIf(it -> System.currentTimeMillis() - (it.getKey() >>> TIMESTAMP_OFFSET) - DISCORD_EPOCH >= TTL),
                1, 1, TimeUnit.HOURS);
    }

    public void open(long messageId, CommandContextImpl ctx) { // Ну тип открыть для прослушивания
        interactionCache.put(messageId, ctx);
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (!interactionCache.containsKey(event.getMessageIdLong())
                || (event.getIdLong() >>> TIMESTAMP_OFFSET) - (event.getMessageIdLong() >>> TIMESTAMP_OFFSET) >= TTL) {
            event.reply("Данное сообщение больше не принимает взаимодействий.").setEphemeral(true).queue();
            CommandContextImpl ctx = interactionCache.remove(event.getMessageIdLong());
            if (ctx != null) ctx.cancelButtonClickWaiting();
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
        context.applyButtonClickEvent(event);
    }
}
