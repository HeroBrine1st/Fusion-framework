package ru.herobrine1st.fusion.internal.listener;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.internal.command.CommandContextImpl;
import ru.herobrine1st.fusion.internal.manager.ThreadPoolProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.utils.TimeUtil.DISCORD_EPOCH;
import static net.dv8tion.jda.api.utils.TimeUtil.TIMESTAMP_OFFSET;

public class ButtonInteractionHandler {
    private final static Logger logger = LoggerFactory.getLogger(ButtonInteractionHandler.class);
    private final static long TTL = 15 * 60 * 1000;
    private final static Map<Long, CommandContextImpl> interactionCache = new HashMap<>();

    static {
        ThreadPoolProvider.getScheduledPool().scheduleAtFixedRate(() -> interactionCache.entrySet()
                        .removeIf(it -> {
                            if (System.currentTimeMillis() - (it.getKey() >>> TIMESTAMP_OFFSET) - DISCORD_EPOCH >= TTL) {
                                it.getValue().cancelButtonClickWaiting();
                                logger.trace("Clearing %s interaction cache due to timeout".formatted(it.getKey()));
                                return true;
                            } else return false;
                        }),
                15, 15, TimeUnit.MINUTES);
    }

    public static void open(long messageId, CommandContextImpl ctx) { // Ну тип открыть для прослушивания
        interactionCache.put(messageId, ctx);
    }

    @SubscribeEvent
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (!interactionCache.containsKey(event.getMessageIdLong())) {
            return;
        } else if ((event.getIdLong() >>> TIMESTAMP_OFFSET) - (event.getMessageIdLong() >>> TIMESTAMP_OFFSET) >= TTL) {
            event.reply("Данное сообщение больше не принимает взаимодействий.").setEphemeral(true).queue();
            CommandContextImpl ctx = interactionCache.remove(event.getMessageIdLong());
            if (ctx != null) ctx.cancelButtonClickWaiting();
            logger.trace("Cancelled ButtonClickEvent due to timeout");
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
        if (context.getEditOriginal())
            event.deferEdit().queue();
        else
            event.deferReply().queue();
        context.applyButtonClickEvent(event);
    }
}
