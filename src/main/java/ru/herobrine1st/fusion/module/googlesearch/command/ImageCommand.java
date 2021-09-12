package ru.herobrine1st.fusion.module.googlesearch.command;

import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.RestAction;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.CommandExecutor;
import ru.herobrine1st.fusion.api.exception.CommandException;
import ru.herobrine1st.fusion.module.googlesearch.Config;
import ru.herobrine1st.fusion.module.googlesearch.network.JsonRequest;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

public class ImageCommand implements CommandExecutor {
    private final Random random = new Random();
    private final static String URL = "https://www.googleapis.com/customsearch/v1";

    private URL getUrl(CommandContext ctx) {
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(URL)).newBuilder()
                .addQueryParameter("num", "10")
                .addQueryParameter("start", "1")
                .addQueryParameter("searchType", "image")
                .addQueryParameter("cx", Config.getCX())
                .addQueryParameter("key", Config.getKey())
                .addQueryParameter("safe", "1")
                .addQueryParameter("q", URLEncoder.encode(ctx.<String>getOne("query").orElseThrow(), StandardCharsets.UTF_8));
        ctx.<String>getOne("type").ifPresent(it -> httpBuilder.addQueryParameter("fileType", it));
        return httpBuilder.build().url();
    }

    private MessageEmbed getEmbedFromImage(JsonObject image, int index, int count) {
        var builder = new EmbedBuilder()
                .setTitle(
                        image.get("title").getAsString(),
                        image.getAsJsonObject("image").get("contextLink").getAsString())
                .setImage(image.get("link").getAsString());
        if(image.get("mime").getAsString().equals("image/svg+xml")) builder.setDescription("SVG images may be not displayed on some clients.");
        return builder.setFooter("Image %s/%s".formatted(index + 1, count))
                .build();
    }

    private RestAction<?> doCycle(CommandContext ctx, JsonObject json, int index) {
        if (json.getAsJsonArray("items") != null && json.getAsJsonArray("items").size() != 0) {
            int size = json.getAsJsonArray("items").size();
            Button prevButton = Button.primary("prev", "< Previous").withDisabled(index == 0);
            Button nextButton = Button.primary("next", "Next >").withDisabled(index == size - 1);
            ActionRow actionRow = ActionRow.of(prevButton, nextButton);
            return ctx.setEditOriginal(true).replyThenWaitUserClick(getEmbedFromImage(
                            json.getAsJsonArray("items").get(index).getAsJsonObject(), index, size), actionRow)
                    .flatMap(it -> {
                        String componentId = it.getComponentId();
                        int newIndex = index;
                        if (componentId.equals("prev")) newIndex--;
                        else if (componentId.equals("next")) newIndex++;
                        return doCycle(ctx, json, newIndex);
                    });
        } else {
            return ctx.replyError("No results found");
        }
    }

    @Override
    public void execute(@NotNull CommandContext ctx) throws CommandException {
        if (Config.getKey() == null || Config.getCX() == null) {
            throw new CommandException("No API key found");
        }

        JsonRequest.makeRequest(getUrl(ctx))
                .flatMap(json -> doCycle(ctx, json,
                        (json.getAsJsonArray("items") != null && json.getAsJsonArray("items").size() != 0) ?
                                random.nextInt(json.getAsJsonArray("items").size()) : 0)
                )
                .queue(null, ctx::replyException);
    }
}
