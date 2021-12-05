package ru.herobrine1st.fusion.internal.listener;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.internal.command.CommandContextImpl;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.utils.TimeUtil.DISCORD_EPOCH;
import static net.dv8tion.jda.api.utils.TimeUtil.TIMESTAMP_OFFSET;

public class ComponentInteractionHandler implements EventListener {
    public final static ComponentInteractionHandler INSTANCE = new ComponentInteractionHandler();
    private final static Logger logger = LoggerFactory.getLogger(ComponentInteractionHandler.class);
    private final static long TTL = 15 * 60 * 1000;
    private final static Map<Long, CommandContextImpl> interactionCache = new HashMap<>();
    static {
        Executors.newScheduledThreadPool(1, it -> {
            var t = new Thread(it);
            t.setDaemon(true);
            return t;
        }).scheduleAtFixedRate(() -> interactionCache.entrySet()
                        .removeIf(it -> {
                            if (System.currentTimeMillis() - (it.getKey() >>> TIMESTAMP_OFFSET) - DISCORD_EPOCH >= TTL) {
                                it.getValue().cancelComponentInteractionWaiting();
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
    public void onComponentInteraction(@NotNull GenericComponentInteractionCreateEvent event) {
        if (!interactionCache.containsKey(event.getMessageIdLong())) {
            event.reply("This message no longer accepts component interactions.").setEphemeral(true).queue();
            return;
        } else if ((event.getIdLong() >>> TIMESTAMP_OFFSET) - (event.getMessageIdLong() >>> TIMESTAMP_OFFSET) >= TTL) {
            event.reply("This message no longer accepts component interactions.").setEphemeral(true).queue();
            CommandContextImpl ctx = interactionCache.remove(event.getMessageIdLong());
            if (ctx != null) ctx.cancelComponentInteractionWaiting();
            logger.trace("Cancelled GenericComponentInteractionCreateEvent due to timeout");
            return;
        }
        var context = interactionCache.get(event.getMessageIdLong());
        if (context.shouldValidateUser() && context.getUser().getIdLong() != event.getUser().getIdLong()) {
            event.reply("You're not a command caller in this context.").setEphemeral(true).queue();
            return;
        }
        Optional<Component> componentOptional = event.getMessage().getActionRows().stream()
                .map(ActionRow::getComponents)
                .flatMap(List::stream)
                .filter(it -> Objects.equals(it.getId(), event.getComponentId()))
                .findAny();
        if (componentOptional.isEmpty()) {
            logger.trace("Ignored event %s - component spoofing detected".formatted(event.getId()));
            return;
        }
        Component component = componentOptional.get();
        if(component instanceof SelectionMenu selectionMenu && event instanceof SelectionMenuEvent selectionMenuEvent) {
            if(!selectionMenu.getOptions().stream().map(SelectOption::getValue).toList().containsAll(selectionMenuEvent.getValues())) {
                logger.trace("Ignored event %s - component spoofing detected".formatted(event.getId()));
                return;
            }
        }
        interactionCache.remove(event.getMessageIdLong());
        context.applyComponentInteractionEvent(event);
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof GenericComponentInteractionCreateEvent genericComponentInteractionCreateEvent)
            onComponentInteraction(genericComponentInteractionCreateEvent);
    }
}
