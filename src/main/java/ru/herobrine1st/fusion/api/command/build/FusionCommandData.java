package ru.herobrine1st.fusion.api.command.build;

import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.FusionOptionData;

public class FusionCommandData<R extends FusionOptionData> extends FusionBaseCommand<FusionCommandData<R>, R> {
    public FusionCommandData(@NotNull String name, @NotNull String description) {
        super(name, description);
    }
}