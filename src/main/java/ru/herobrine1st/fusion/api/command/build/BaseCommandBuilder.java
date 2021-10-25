package ru.herobrine1st.fusion.api.command.build;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.FusionBaseCommand;
import ru.herobrine1st.fusion.api.command.FusionOptionData;
import ru.herobrine1st.fusion.api.command.PermissionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract sealed class BaseCommandBuilder<T extends BaseCommandBuilder<T, R>, R extends FusionOptionData>
        extends OptionBuilder<BaseCommandBuilder<T, R>>
        permits CommandBuilder, SubcommandBuilder {

    protected final List<R> options = new ArrayList<>();
    protected String shortName;
    protected PermissionHandler permissionHandler = PermissionHandler.DEFAULT;

    protected BaseCommandBuilder(@NotNull String name, @NotNull String description) {
        super(name, description);
        this.shortName = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1).toLowerCase(Locale.ROOT);
    }

    @NotNull
    @SafeVarargs
    public final T addOptions(R... options) {
        Checks.notNull(options, "Options");
        Checks.noneNull(options, "Option");
        Checks.notEmpty(options, "Options");
        this.options.addAll(Arrays.asList(options));
        //noinspection unchecked
        return (T) this;
    }

    public T setShortName(String shortName) {
        this.shortName = shortName;
        //noinspection unchecked
        return (T) this;
    }

    public T setPermissionHandler(PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
        //noinspection unchecked
        return (T) this;
    }

    @Override
    public abstract FusionBaseCommand<R> build();
}
