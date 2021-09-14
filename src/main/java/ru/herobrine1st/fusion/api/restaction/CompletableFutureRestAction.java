package ru.herobrine1st.fusion.api.restaction;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class CompletableFutureRestAction<R> implements RestAction<R> {
    private final static Logger logger = LoggerFactory.getLogger(CompletableFutureRestAction.class);
    private final CompletableFuture<R> completableFuture;
    private BooleanSupplier checks = null;

    @Contract(value = "_ -> new", pure = true)
    public static <R> @NotNull CompletableFutureRestAction<R> of(CompletableFuture<R> completableFuture) {
        return new CompletableFutureRestAction<>(completableFuture);
    }

    private CompletableFutureRestAction(CompletableFuture<R> completableFuture) {
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

    private static <T> void tryOrElse(Consumer<T> consumer, T t, Consumer<? super Throwable> failure) {
        try {
            if (consumer != null)
                consumer.accept(t);
        } catch (Throwable e) {
            logger.error("Exception in handler", e);
            if (failure != null)
                failure.accept(e);
        }
    }

    @Override
    public void queue(@Nullable Consumer<? super R> success, @Nullable Consumer<? super Throwable> failure) {
        completableFuture.whenCompleteAsync((result, throwable) -> {
            if (result != null && success != null) tryOrElse(success, result, failure);
            if (throwable != null && failure != null) tryOrElse(failure, throwable, null);
        });
    }

    @Override
    public R complete(boolean shouldQueue) {
        try {
            return completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public CompletableFuture<R> submit(boolean shouldQueue) {
        return completableFuture;
    }
}
