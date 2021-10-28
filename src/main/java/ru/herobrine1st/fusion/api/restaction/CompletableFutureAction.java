package ru.herobrine1st.fusion.api.restaction;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class CompletableFutureAction<R> implements RestAction<R> {
    private final static Logger logger = LoggerFactory.getLogger(CompletableFutureAction.class);
    private final CompletableFuture<R> completableFuture;
    private BooleanSupplier checks = null;

    @Contract(value = "_ -> new", pure = true)
    public static <R> @NotNull CompletableFutureAction<R> of(CompletableFuture<R> completableFuture) {
        return new CompletableFutureAction<>(completableFuture);
    }

    private CompletableFutureAction(CompletableFuture<R> completableFuture) {
        this.completableFuture = completableFuture;
    }

    @NotNull
    @Override
    public JDA getJDA() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public RestAction<R> setCheck(@Nullable BooleanSupplier checks) {
        this.checks = checks;
        return this;
    }

    @Override
    public BooleanSupplier getCheck() {
        return checks;
    }

    private static <T> void tryCallback(Consumer<T> consumer, T t) {
        if (consumer != null)
            try {
                consumer.accept(t);
            } catch (Throwable e) {
                logger.error("Exception in handler", e);
            }
    }

    @Override
    public void queue(@Nullable Consumer<? super R> success, @Nullable Consumer<? super Throwable> failure) {
        completableFuture.whenCompleteAsync((result, throwable) -> {
            if (result != null) tryCallback(success, result);
            if (throwable != null) tryCallback(failure, throwable);
        });
    }

    @Override
    public R complete(boolean shouldQueue) {
        return completableFuture.join();
    }

    @NotNull
    @Override
    public CompletableFuture<R> submit(boolean shouldQueue) {
        return completableFuture.toCompletableFuture();
    }
}
