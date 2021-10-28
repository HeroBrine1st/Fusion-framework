package ru.herobrine1st.fusion.api.command.build;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.option.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.option.FusionOptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract sealed class BaseCommandBuilder<T extends BaseCommandBuilder<T, R>, R extends FusionOptionData>
        extends OptionBuilder<BaseCommandBuilder<T, R>>
        permits SubcommandBuilder, WithArgumentsBuilder, WithSubcommandGroupsBuilder, WithSubcommandsBuilder {

    protected final List<R> options = new ArrayList<>();
    protected PermissionHandler permissionHandler = PermissionHandler.DEFAULT;

    protected BaseCommandBuilder(@NotNull String name, @NotNull String description) {
        super(name, description);
    }


    @NotNull
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final T addOptions(R... options) {
        Checks.notNull(options, "Options");
        Checks.noneNull(options, "Option");
        Checks.notEmpty(options, "Options");
        this.options.addAll(Arrays.asList(options));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setPermissionHandler(PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
        return (T) this;
    }

    @Override
    public abstract FusionBaseCommand<R> build();
}
