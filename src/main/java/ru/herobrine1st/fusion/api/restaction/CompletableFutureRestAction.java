package ru.herobrine1st.fusion.api.restaction;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class CompletableFutureRestAction<R> implements RestAction<R> {

    private final CompletableFuture<R> completableFuture;
    private BooleanSupplier checks = null;

    public static <R> CompletableFutureRestAction<R> of(CompletableFuture<R> completableFuture) {
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

    @Override
    public void queue(@Nullable Consumer<? super R> success, @Nullable Consumer<? super Throwable> failure) {
        completableFuture.whenCompleteAsync((result, throwable) -> {
            if (result != null && success != null) success.accept(result);
            if (throwable != null && failure != null) failure.accept(throwable);
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
